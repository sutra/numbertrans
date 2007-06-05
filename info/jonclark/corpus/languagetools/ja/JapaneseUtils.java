package info.jonclark.corpus.languagetools.ja;

public class JapaneseUtils {

	public static final int FIRST_MAIN_HIRAGANA = 0x3041;
	public static final int LAST_MAIN_HIRAGANA = 0x3096;
	public static final int FIRST_MAIN_KATAKANA = 0x30A1;
	public static final int LAST_MAIN_KATAKANA = 0x30F6;

	/**
	 * Emulates furigana, but with replacement
	 */
	public static String katakanaToHiragana(String str) {

		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] >= FIRST_MAIN_KATAKANA && arr[i] <= LAST_MAIN_KATAKANA) {
				arr[i] = (char) (arr[i] - FIRST_MAIN_KATAKANA + FIRST_MAIN_HIRAGANA);
			} else if (i > 0 && arr[i] == 'ー') {
				// deal with vowel extenders
				switch (arr[i - 1]) {
				
				// a
				case 'ぁ':
				case 'あ':
				case 'か':
				case 'さ':
				case 'た':
				case 'な':
				case 'は':
				case 'ま':
				case 'や':
				case 'ら':
				case 'わ':
				case 'が':
				case 'ざ':
				case 'だ':
				case 'ば':
				case 'ぱ':
				case 'ゃ':
				case 'ゎ':
					arr[i] = 'あ';
					break;
					
				// i
				case 'ぃ':
				case 'い':
				case 'き':
				case 'し':
				case 'ち':
				case 'に':
				case 'ひ':
				case 'み':
				case 'り':
				case 'ゐ':
				case 'ぎ':
				case 'じ':
				case 'ぢ':
				case 'び':
				case 'ぴ':
					arr[i] = 'い';
					break;

				// u
				case 'ぅ':
				case 'う':
				case 'く':
				case 'す':
				case 'つ':
				case 'ぬ':
				case 'ふ':
				case 'む':
				case 'ゆ':
				case 'る':
				case 'ぐ':	
				case 'ず':
				case 'づ':
				case 'ぶ':
				case 'ぷ':
				case 'ゅ':
					arr[i] = 'う';
					break;

				// e
				case 'ぇ':
				case 'え':
				case 'け':
				case 'せ':
				case 'て':
				case 'ね':
				case 'へ':
				case 'め':
				case 'れ':
				case 'ゑ':
				case 'げ':	
				case 'ぜ':
				case 'で':
				case 'べ':
				case 'ぺ':
					arr[i] = 'い'; // note use of i
					break;

				// o
				case 'ぉ':
				case 'お':
				case 'こ':
				case 'そ':
				case 'と':
				case 'の':
				case 'ほ':
				case 'も':
				case 'よ':
				case 'ろ':
				case 'を':
				case 'ご':
				case 'ぞ':
				case 'ど':
				case 'ぼ':
				case 'ぽ':
				case 'ょ':
					arr[i] = 'う'; // note use of u
					break;
				}
			}
		}

		return new String(arr);
	}
}
