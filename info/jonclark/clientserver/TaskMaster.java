/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.clientserver;

import info.jonclark.stat.SecondTimer;
import info.jonclark.util.PropertiesException;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskMaster {
    private final ConcurrentLinkedQueue<String> queueTasks = new ConcurrentLinkedQueue<String>();
    private final ConcurrentLinkedQueue<SimpleClient> disconnectedWorkers = new ConcurrentLinkedQueue<SimpleClient>();
    private final ConcurrentLinkedQueue<SimpleClient> idleWorkers = new ConcurrentLinkedQueue<SimpleClient>();

    private static final long CONNECTOR_THREAD_WAIT = 5000;
    private static final long TASKER_THREAD_WAIT = 50;
    private static final long BLOCKER_THREAD_WAIT = 1000;

    private final ExecutorService taskExecutor = Executors.newCachedThreadPool();
    private final ExecutorService reconnectExecutor = Executors.newCachedThreadPool();

    private final AtomicInteger nTasksCompleted = new AtomicInteger(0);

    private final String desiredTask;
    private boolean running;

    private final Logger log = Logger.getLogger("TASK_MASTER");
    private final SecondTimer timer = new SecondTimer(true);

    // TODO: Add a timeout so that each worker has a certain amount of time
    // to complete its task. The amount of time allowed for each task should
    // be increased after each timeout.

    // TODO: Allow multiple connections to a single host via the config file

    public TaskMaster(String desiredTask, Properties props) throws PropertiesException {
	assert desiredTask != null : "desiredTask cannot be null";
	assert props != null : "props cannot be null";

	String[] workerHosts = StringUtils.tokenize(props.getProperty("workers"), "; ");
	for (final String workerHost : workerHosts) {
	    String[] tokens = StringUtils.tokenize(workerHost, ":");
	    if (tokens.length != 2) {
		throw new PropertiesException("Malformed worker list token: " + workerHost);
	    }
	    int hostPort = Integer.parseInt(tokens[1]);
	    disconnectedWorkers.add(new SimpleClient(tokens[0], hostPort));
	}

	this.desiredTask = desiredTask;

	running = true;
	connectorThread.start();
	taskerThread.start();
	timer.go();
    }

    public void performTask(final String task) {
	log.finest("Adding task: " + task);
	queueTasks.add(task);
    }

    /**
         * Blocks until all tasks have completed.
         */
    public void waitForAllTasks(boolean shutdownOnCompletion) {
	boolean done = false;
	while (!done) {
	    try {
		if (queueTasks.isEmpty()) {
		    done = true;
		}
		Thread.sleep(BLOCKER_THREAD_WAIT);
	    } catch (InterruptedException e) {
		log.info("waitForAllTasks() interrupted from sleep()");
	    }
	}

	if (shutdownOnCompletion)
	    shutdown();
    }

    /**
         * Enables user interaction while waiting on tasks to complete.
         * 
         * @param shutdownOnCompletion
         */
    public void runConsole() {
	Thread thread = new Thread(new Runnable() {
	    public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		final String prompt = "TaskMaster> ";
		String line = null;
		try {
		    System.out.print(prompt);
		    boolean quitRequested = false;
		    while (!quitRequested && (line = in.readLine()) != null) {
			if (line.equals("help")) {
			    System.out.println("tasksleft taskscompleted workersidle "
				    + "workersdisconnected rate loglevel quit");
			} else if (line.equals("tasksleft")) {
			    System.out.println("Tasks remaining: " + queueTasks.size());
			} else if (line.equals("taskscompleted")) {
			    System.out.println("Tasks completed: " + nTasksCompleted.intValue());
			} else if (line.equals("workersidle")) {
			    System.out.println("Workers idle: " + idleWorkers.size());
			} else if (line.equals("workersdisconnected")) {
			    System.out.println("Workers disconnected: "
				    + disconnectedWorkers.size());
			    for (final SimpleClient client : disconnectedWorkers) {
				System.out.println(client.toString());
			    }
			} else if (line.equals("rate")) {
			    System.out.println("Service rate: "
				    + timer.getEventsPerSecond(nTasksCompleted.intValue())
				    + " tasks/sec");
			} else if (line.equals("quit")) {
			    quitRequested = true;
			} else if (line.startsWith("loglevel")) {
			    String[] tokens = StringUtils.tokenize(line);
			    try {
				Level level = Level.parse(tokens[1]);
				log.setLevel(level);
				System.out.println("Log level is now "+ level);
			    } catch (IllegalArgumentException e) {
				System.out.println("Bad log level.");
			    }
			} else {
			    System.out.println("Unrecognized command: " + line);
			}
			if (!quitRequested)
			    System.out.print(prompt);
		    }
		} catch (IOException e) {
		    // this seems appropriate since we're in console mode
		    e.printStackTrace();
		}
	    }
	});
	thread.start();
    }

    public void shutdown() {
	running = false;

	connectorThread.interrupt();
	taskerThread.interrupt();
	taskExecutor.shutdown();
	reconnectExecutor.shutdown();

	for (final SimpleClient client : idleWorkers) {
	    client.disconnect();
	}
    }

    private Runnable workerConnector = new Runnable() {
	public void run() {

	    // TODO: Create string constants for protocol messages

	    SimpleClient client = disconnectedWorkers.poll();

	    // we're not guaranteed to have a client here due to concurrency
	    if (client != null) {
		log.finer("Attempting connection to " + client.toString());

		try {
		    client.connect();

		    String line = client.getMessage();
		    String[] tokens = StringUtils.tokenize(line);

		    log.finest("Received message: " + line);

		    if (tokens.length == 2 && tokens[0].equals("CAN:")
			    && tokens[1].equals(desiredTask)) {
			log.info("Connection to " + client.toString() + " sucessful.");
			boolean bAdded = idleWorkers.add(client);
			assert bAdded == true : "Unable to add worker to idleWorkers";
		    } else {
			client.sendMessage("ERROR: " + "WRONG_TASK");
			client.disconnect();
			log.warning("Worker " + client.toString() + " does not support the task "
				+ desiredTask);
			disconnectedWorkers.add(client);
		    }
		} catch (ConnectionException e) {
		    client.disconnect();
		    log.warning("Connection to " + client.toString() + " failed.");
		    log.warning(StringUtils.getStackTrace(e));
		    disconnectedWorkers.add(client);
		}
	    }
	}
    };

    private Runnable workerTasker = new Runnable() {
	public void run() {
	    final String task = queueTasks.poll();

	    if (task != null) {
		final SimpleClient client = idleWorkers.poll();

		if (client != null) {
		    log.info("Tasking " + client.toString() + " with task: " + task);
		    client.sendMessage("TASK: " + task);
		    try {
			String reply = client.getMessage();
			String[] tokens = StringUtils.tokenize(reply);
			if (tokens.length == 2 && tokens[0].equals("RESULT:")
				&& tokens[1].equals("TRUE")) {
			    log.info("Task sucessful: " + task);
			    synchronized (nTasksCompleted) {
				nTasksCompleted.incrementAndGet();
			    }
			} else {
			    log.info("Task failed: " + task);
			}
			idleWorkers.add(client);
		    } catch (ConnectionException e) {
			// we need to try this task over again...
			log.info("Task requeued: " + task);
			queueTasks.add(task);

			client.disconnect();
			disconnectedWorkers.add(client);
		    }
		} else {
		    // no client was available, try again later
		    taskExecutor.execute(this);
		}

	    } // end if task != null
	}
    };

    private Thread connectorThread = new Thread(new Runnable() {
	public void run() {
	    while (running) {

		try {
		    if (!disconnectedWorkers.isEmpty()) {
			final int nSize = disconnectedWorkers.size();
			for (int i = 0; i < nSize; i++)
			    reconnectExecutor.execute(workerConnector);
		    }
		    Thread.sleep(CONNECTOR_THREAD_WAIT);
		} catch (InterruptedException e) {
		    log.finer("connectorThread interrupted from sleep()");
		}

	    }
	}
    });

    private Thread taskerThread = new Thread(new Runnable() {
	public void run() {
	    while (running) {

		try {
		    if (!idleWorkers.isEmpty()) {
			final int nSize = idleWorkers.size();
			for (int i = 0; i < nSize; i++)
			    taskExecutor.execute(workerTasker);
		    }
		    Thread.sleep(TASKER_THREAD_WAIT);
		} catch (InterruptedException e) {
		    log.finer("taskerThread interrupted from sleep()");
		}

	    }
	}
    });

}
