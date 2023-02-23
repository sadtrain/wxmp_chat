package com.honghu.wxmp_chat.utils;

import toolgood.words.IllegalWordsSearch;

public class IllegalWorkUtil {
    static IllegalWordsSearch search = new IllegalWordsSearch();

    public static boolean containsIllegalWord(String response) {
        return search.ContainsAny(response);
    }
}
