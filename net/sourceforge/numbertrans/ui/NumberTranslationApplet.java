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

package net.sourceforge.numbertrans.ui;


import javax.swing.JApplet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

import net.sourceforge.numbertrans.framework.base.GeneralNumber;
import net.sourceforge.numbertrans.framework.parser.NumberParser;
import net.sourceforge.numbertrans.framework.scribe.NumberScribe;
import net.sourceforge.numbertrans.framework.scribe.NumberScribe.Form;
import net.sourceforge.numbertrans.languages.arabic.HinduArabicCardinalParser;
import net.sourceforge.numbertrans.languages.arabic.HinduArabicCardinalScribe;
import net.sourceforge.numbertrans.languages.bengali.BengaliCardinalParser;
import net.sourceforge.numbertrans.languages.bengali.BengaliCardinalScribe;
import net.sourceforge.numbertrans.languages.chinese.ChineseCardinalParser;
import net.sourceforge.numbertrans.languages.chinese.ChineseFractionParser;
import net.sourceforge.numbertrans.languages.english.EnglishCardinalParser;
import net.sourceforge.numbertrans.languages.english.EnglishCardinalScribe;
import net.sourceforge.numbertrans.languages.english.EnglishFractionParser;
import net.sourceforge.numbertrans.languages.english.EnglishFractionScribe;
import net.sourceforge.numbertrans.languages.english.EnglishOrdinalScribe;
import net.sourceforge.numbertrans.languages.gujarati.GujaratiCardinalParser;
import net.sourceforge.numbertrans.languages.gujarati.GujaratiCardinalScribe;
import net.sourceforge.numbertrans.languages.gurumukhi.GurumukhiCardinalParser;
import net.sourceforge.numbertrans.languages.gurumukhi.GurumukhiCardinalScribe;
import net.sourceforge.numbertrans.languages.japanese.JapaneseCardinalScribe;
import net.sourceforge.numbertrans.languages.japanese.JapaneseFractionParser;
import net.sourceforge.numbertrans.languages.japanese.JapaneseFractionScribe;
import net.sourceforge.numbertrans.languages.kannada.KannadaCardinalParser;
import net.sourceforge.numbertrans.languages.kannada.KannadaCardinalScribe;
import net.sourceforge.numbertrans.languages.korean.KoreanCardinalScribe;
import net.sourceforge.numbertrans.languages.malayalam.MalayalamCardinalParser;
import net.sourceforge.numbertrans.languages.malayalam.MalayalamCardinalScribe;
import net.sourceforge.numbertrans.languages.oriya.OriyaCardinalParser;
import net.sourceforge.numbertrans.languages.oriya.OriyaCardinalScribe;
import net.sourceforge.numbertrans.languages.roman.RomanCardinalParser;
import net.sourceforge.numbertrans.languages.roman.RomanCardinalScribe;
import net.sourceforge.numbertrans.languages.tamil.TamilCardinalParser;
import net.sourceforge.numbertrans.languages.tamil.TamilCardinalScribe;
import net.sourceforge.numbertrans.languages.telugu.TeluguCardinalParser;
import net.sourceforge.numbertrans.languages.telugu.TeluguCardinalScribe;
import net.sourceforge.numbertrans.languages.thai.ThaiCardinalParser;
import net.sourceforge.numbertrans.languages.thai.ThaiCardinalScribe;
import net.sourceforge.numbertrans.languages.tibetan.TibetanCardinalParser;
import net.sourceforge.numbertrans.languages.tibetan.TibetanCardinalScribe;

public class NumberTranslationApplet extends JApplet {

    private static final long serialVersionUID = -1281996307622163206L;
    private JPanel jPanel = null;
    private TextArea textInput = null;
    private TextArea textOutput = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel11 = null;
    private JLabel labelSupportedTypes = null;
    private JComboBox comboInputLanguage = null;
    private JComboBox comboOutputLanguage = null;
    private JButton butGo = null;

    public static final String HINDU_ARABIC_CARDINAL = "Hindu-Arabic Cardinal Number";
    public static final String BENGALI_CARDINAL = "Bengali Cardinal Number";
    public static final String CHINESE_CARDINAL = "Chinese Cardinal Number";
    public static final String CHINESE_FRACTION = "Chinese Fraction";
    public static final String ENGLISH_CARDINAL = "English Cardinal Number";
    public static final String ENGLISH_FRACTION = "English Fraction";
    public static final String ENGLISH_ORDINAL = "English Ordinal";
    public static final String GUJARATI_CARDINAL = "Gujarati Cardinal Number";
    public static final String GURUMUKHI_CARDINAL = "Gurumukhi Cardinal Number";
    public static final String JAPANESE_CARDINAL = "Japanese Cardinal Number";
    public static final String JAPANESE_FRACTION = "Japanese Fraction";
    public static final String KANNADA_CARDINAL = "Kannada Cardinal Number";
    public static final String KOREAN_CARDINAL = "Korean Cardinal Number";
    public static final String MALAYALAM_CARDINAL = "Malayalam Cardinal Number";
    public static final String ORIYA_CARDINAL = "Oriya Cardinal Number";
    public static final String ROMAN_CARDINAL = "Roman Numeral";
    public static final String TAMIL_CARDINAL = "Tamil Cardinal Number";
    public static final String TELUGU_CARDINAL = "Telugu Cardinal Number";
    public static final String THAI_CARDINAL = "Thai Cardinal Number";
    public static final String TIBETAN_CARDINAL = "Tibetan Cardinal Number";

    /**
         * This method initializes
         */
    public NumberTranslationApplet() {
	super();

    }

    /**
         * This method initializes this
         */
    public void init() {
	this.setSize(new Dimension(700, 400));
	this.setContentPane(getJPanel());

    }

    /**
         * This method initializes jPanel
         * 
         * @return javax.swing.JPanel
         */
    private JPanel getJPanel() {
	if (jPanel == null) {
	    labelSupportedTypes = new JLabel();
	    labelSupportedTypes.setBounds(new Rectangle(181, 31, 250, 16));
	    labelSupportedTypes.setText("Cardinals");
	    jLabel11 = new JLabel();
	    jLabel11.setBounds(new Rectangle(16, 270, 74, 16));
	    jLabel11.setText("Output:");
	    jLabel1 = new JLabel();
	    jLabel1.setBounds(new Rectangle(21, 81, 78, 16));
	    jLabel1.setText("Input:");
	    jLabel = new JLabel();
	    jLabel.setBounds(new Rectangle(10, 30, 157, 16));
	    jLabel.setText("Supported Number Types:");
	    jPanel = new JPanel();
	    jPanel.setLayout(null);
	    jPanel.add(getTextInput(), null);
	    jPanel.add(getTextOutput(), null);
	    jPanel.add(jLabel, null);
	    jPanel.add(jLabel1, null);
	    jPanel.add(jLabel11, null);
	    jPanel.add(labelSupportedTypes, null);
	    jPanel.add(getComboInputLanguage(), null);
	    jPanel.add(getComboOutputLanguage(), null);
	    jPanel.add(getButGo(), null);
	    jPanel.setBackground(Color.WHITE);
	}
	return jPanel;
    }

    /**
         * This method initializes textInput
         * 
         * @return javax.swing.JTextArea
         */
    private TextArea getTextInput() {
	if (textInput == null) {
	    textInput = new TextArea();
	    textInput.setBounds(new Rectangle(103, 74, 342, 105));
	    textInput.setFont(new Font(null, Font.BOLD, 20));
	}
	return textInput;
    }

    /**
         * This method initializes textOutput
         * 
         * @return javax.swing.JTextArea
         */
    private TextArea getTextOutput() {
	if (textOutput == null) {
	    textOutput = new TextArea();
	    textOutput.setBounds(new Rectangle(104, 264, 338, 101));
	    textOutput.setFont(new Font(null, Font.BOLD, 20));
	}
	return textOutput;
    }

    /**
         * This method initializes comboInputLanguage
         * 
         * @return javax.swing.JComboBox
         */
    private JComboBox getComboInputLanguage() {
	if (comboInputLanguage == null) {
	    comboInputLanguage = new JComboBox();
	    comboInputLanguage.setBounds(new Rectangle(456, 83, 225, 25));

	    comboInputLanguage.addItem(HINDU_ARABIC_CARDINAL);
	    comboInputLanguage.addItem(BENGALI_CARDINAL);
	    comboInputLanguage.addItem(CHINESE_CARDINAL);
	    comboInputLanguage.addItem(CHINESE_FRACTION);
	    comboInputLanguage.addItem(ENGLISH_CARDINAL);
	    comboInputLanguage.addItem(ENGLISH_FRACTION);
	    comboInputLanguage.addItem(GUJARATI_CARDINAL);
	    comboInputLanguage.addItem(GURUMUKHI_CARDINAL);
	    comboInputLanguage.addItem(JAPANESE_FRACTION);
	    comboInputLanguage.addItem(KANNADA_CARDINAL);
	    comboInputLanguage.addItem(MALAYALAM_CARDINAL);
	    comboInputLanguage.addItem(ORIYA_CARDINAL);
	    comboInputLanguage.addItem(ROMAN_CARDINAL);
	    comboInputLanguage.addItem(TAMIL_CARDINAL);
	    comboInputLanguage.addItem(TELUGU_CARDINAL);
	    comboInputLanguage.addItem(THAI_CARDINAL);
	    comboInputLanguage.addItem(TIBETAN_CARDINAL);
	}
	return comboInputLanguage;
    }

    /**
         * This method initializes comboOutputLanguage
         * 
         * @return javax.swing.JComboBox
         */
    private JComboBox getComboOutputLanguage() {
	if (comboOutputLanguage == null) {
	    comboOutputLanguage = new JComboBox();
	    comboOutputLanguage.setBounds(new Rectangle(458, 281, 225, 25));

	    comboOutputLanguage.addItem(HINDU_ARABIC_CARDINAL);
	    comboOutputLanguage.addItem(BENGALI_CARDINAL);
	    comboOutputLanguage.addItem(CHINESE_FRACTION);
	    comboOutputLanguage.addItem(ENGLISH_CARDINAL);
	    comboOutputLanguage.addItem(ENGLISH_FRACTION);
	    comboOutputLanguage.addItem(ENGLISH_ORDINAL);
	    comboOutputLanguage.addItem(GUJARATI_CARDINAL);
	    comboOutputLanguage.addItem(GURUMUKHI_CARDINAL);
	    comboOutputLanguage.addItem(JAPANESE_CARDINAL);
	    comboOutputLanguage.addItem(JAPANESE_FRACTION);
	    comboOutputLanguage.addItem(KANNADA_CARDINAL);
	    comboOutputLanguage.addItem(KOREAN_CARDINAL);
	    comboOutputLanguage.addItem(MALAYALAM_CARDINAL);
	    comboOutputLanguage.addItem(ORIYA_CARDINAL);
	    comboOutputLanguage.addItem(ROMAN_CARDINAL);
	    comboOutputLanguage.addItem(TAMIL_CARDINAL);
	    comboOutputLanguage.addItem(TELUGU_CARDINAL);
	    comboOutputLanguage.addItem(THAI_CARDINAL);
	    comboOutputLanguage.addItem(TIBETAN_CARDINAL);
	}
	return comboOutputLanguage;
    }

    /**
         * This method initializes butGo
         * 
         * @return javax.swing.JButton
         */
    private JButton getButGo() {
	if (butGo == null) {
	    butGo = new JButton(new AbstractAction() {
		private static final long serialVersionUID = 681759442161710474L;

		public void actionPerformed(ActionEvent e) {
		    doTranslation();
		}
	    });
	    butGo.setBounds(new Rectangle(200, 200, 98, 27));
	    butGo.setText("Translate");
	}
	return butGo;
    }

    public void doTranslation() {
	NumberParser parser = null;
	NumberScribe scribe = null;

	Object selectedInput = comboInputLanguage.getSelectedItem();
	if (selectedInput.equals(HINDU_ARABIC_CARDINAL)) {
	    parser = new HinduArabicCardinalParser();
	} else if (selectedInput.equals(BENGALI_CARDINAL)) {
	    parser = new BengaliCardinalParser();
	} else if (selectedInput.equals(CHINESE_CARDINAL)) {
	    parser = new ChineseCardinalParser();
	} else if (selectedInput.equals(CHINESE_FRACTION)) {
	    parser = new ChineseFractionParser();
	}  else if (selectedInput.equals(ENGLISH_CARDINAL)) {
	    parser = new EnglishCardinalParser();
	} else if (selectedInput.equals(ENGLISH_FRACTION)) {
	    parser = new EnglishFractionParser();
	} else if (selectedInput.equals(GUJARATI_CARDINAL)) {
	    parser = new GujaratiCardinalParser();
	} else if (selectedInput.equals(GURUMUKHI_CARDINAL)) {
	    parser = new GurumukhiCardinalParser();
	} else if (selectedInput.equals(JAPANESE_FRACTION)) {
	    parser = new JapaneseFractionParser();
	} else if (selectedInput.equals(KANNADA_CARDINAL)) {
	    parser = new KannadaCardinalParser();
	} else if (selectedInput.equals(MALAYALAM_CARDINAL)) {
	    parser = new MalayalamCardinalParser();
	} else if (selectedInput.equals(ORIYA_CARDINAL)) {
	    parser = new OriyaCardinalParser();
	} else if (selectedInput.equals(ROMAN_CARDINAL)) {
	    parser = new RomanCardinalParser();
	} else if (selectedInput.equals(TAMIL_CARDINAL)) {
	    parser = new TamilCardinalParser();
	} else if (selectedInput.equals(TELUGU_CARDINAL)) {
	    parser = new TeluguCardinalParser();
	} else if (selectedInput.equals(THAI_CARDINAL)) {
	    parser = new ThaiCardinalParser();
	} else if (selectedInput.equals(TIBETAN_CARDINAL)) {
	    parser = new TibetanCardinalParser();
	} else {
	    assert false : "No input language selected.";
	}
	assert parser != null : "No parser selected.";

	Object selectedOutput = comboOutputLanguage.getSelectedItem();
	if (selectedOutput.equals(HINDU_ARABIC_CARDINAL)) {
	    scribe = new HinduArabicCardinalScribe(Form.SHORT,
		    HinduArabicCardinalScribe.DigitSet.OTTOMAN_TURKISH_DIGITS);
	} else if (selectedOutput.equals(BENGALI_CARDINAL)) {
	    scribe = new BengaliCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(ENGLISH_CARDINAL)) {
	    scribe = new EnglishCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(ENGLISH_FRACTION)) {
	    scribe = new EnglishFractionScribe(Form.SHORT);
	} else if (selectedOutput.equals(ENGLISH_ORDINAL)) {
	    scribe = new EnglishOrdinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(GUJARATI_CARDINAL)) {
	    scribe = new GujaratiCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(GURUMUKHI_CARDINAL)) {
	    scribe = new GurumukhiCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(JAPANESE_CARDINAL)) {
	    scribe = new JapaneseCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(JAPANESE_FRACTION)) {
	    scribe = new JapaneseFractionScribe(Form.SHORT);
	} else if (selectedOutput.equals(KANNADA_CARDINAL)) {
	    scribe = new KannadaCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(KOREAN_CARDINAL)) {
	    scribe = new KoreanCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(MALAYALAM_CARDINAL)) {
	    scribe = new MalayalamCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(ORIYA_CARDINAL)) {
	    scribe = new OriyaCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(ROMAN_CARDINAL)) {
	    scribe = new RomanCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(TAMIL_CARDINAL)) {
	    scribe = new TamilCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(TELUGU_CARDINAL)) {
	    scribe = new TeluguCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(THAI_CARDINAL)) {
	    scribe = new ThaiCardinalScribe(Form.SHORT);
	} else if (selectedOutput.equals(TIBETAN_CARDINAL)) {
	    scribe = new TibetanCardinalScribe(Form.SHORT);
	} else {
	    assert false : "No output language selected.";
	}
	assert scribe != null : "No scribe selected.";

	final String input = textInput.getText();
	assert input != null;
	assert input.length() > 0;

	final GeneralNumber number = parser.getNumberFromString(input);
	final String result = scribe.getNumberString(number);

	textOutput.setText(result);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
