package com.honghu.wxmp_chat.utils;

import toolgood.words.IllegalWordsSearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IllegalWorkUtil {
    static IllegalWordsSearch search = new IllegalWordsSearch();

    static Set<String> files = new HashSet<>();
    static {
        files.add("/sensi_words.txt");
        files.add("/keywords.txt");
        Set<String> keywords = new HashSet<>();

        for (String file : files) {
            InputStream resourceAsStream = IllegalWorkUtil.class.getResourceAsStream(file);
            assert resourceAsStream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            reader.lines().forEach(keywords::add);
        }
        search.SetKeywords(new ArrayList<>(keywords));
    }

    public static boolean containsIllegalWord(String response) {
        return search.ContainsAny(response);
    }

    public static void main(String[] args) {
        System.out.println(containsIllegalWord("习近平"));
    }
}
