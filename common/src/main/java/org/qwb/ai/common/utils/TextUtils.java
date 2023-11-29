package org.qwb.ai.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    /** 句子结束符. */
    private static final String SEPARATOR_REGEX = "[，,.?!。？！]";

    /** 字数限制(包含标点). */
    private static final Integer SIZE = 120;

    public static String[] splitSentence(String sentence) {
        // 1. 定义匹配模式
        Pattern p = Pattern.compile(SEPARATOR_REGEX);
        Matcher m = p.matcher(sentence);

        // 2. 拆分句子[拆分后的句子符号也没了]
        String[] words = p.split(sentence);

        // 3. 保留原来的分隔符
        if (words.length > 0) {
            int count = 0;
            while (count < words.length) {
                if (m.find()) {
                    words[count] += m.group();
                }
                count++;
            }
        }
        return words;
    }

    /**
     * 限制拆分后的句子的字数包括分隔符在10位以内
     *
     * 例如： 句子：很抱歉打扰到您了，祝您生活愉快，再见。 根据分隔符拆分后： 很抱歉打扰到您了， 祝您生活愉快， 再见。 限制字数后： 很抱歉打扰到您了，
     * 祝您生活愉快，再见。
     *
     * @param words
     * @return
     */
    public static List<String> limitNumber(String[] words) {
        // 1. 存储限制字数的数据
        List<String> wordList = new ArrayList<>();

        // 2. 限制字数在SIZE以内
        int wordsLength = words.length;
        for (int i = 0; i < wordsLength; i++) {
            // 循环获取拆分后的每个句子
            String word = words[i];

            // 每个句子的长度
            int length = word.length();

            // 当字数>=10，直接存储到wordList
            if (length >= SIZE) {
                wordList.addAll(splitList(word, SIZE));
            }
            else{
                while(i + 1 < wordsLength && word.length() + words[i + 1].length() < SIZE){
                    word = word + words[i + 1];
                    i++;
                }
                wordList.add(word);
            }
        }
        return wordList;
    }

    public static List<String> splitList(String word, int pageSize) {
        List<String> listArray = new ArrayList<>();
        int listSize = word.length();
        for (int i = 0; i < listSize; i += pageSize) {
            int toIndex = Math.min(i + pageSize, listSize);
            listArray.add(word.substring(i, toIndex));
        }
        return listArray;
    }
    
}