package com.starfall.util;

import com.starfall.entity.Search;

import java.lang.reflect.Method;

public class ContentUtil {
    public static String parseContent(String content){
        return content
                .replaceAll("<[^>]*>", "")// 移除HTML标签
                .replaceAll("!\\[.*?\\]\\(.*?\\)", "")// 移除图片内容
                .replaceAll("\\[.*?\\]\\((.*?)\\)", " $1 ")// 处理链接：保留URL，移除显示文字
                .replaceAll("\\*\\*(.*?)\\*\\*", " $1 ")// 移除粗体标签1
                .replaceAll("__(.*?)__", " $1 ")// 移除粗体标签2
                .replaceAll("\\*(.*?)\\*", " $1 ")// 移除删除线标签1
                .replaceAll("_(.*?)_", " $1 ")// 移除删除线标签2
                .replaceAll("\\^(.*?)\\^", " $1 ")// 移除上标标签
                .replaceAll("~(.*?)~", " $1 ")// 移除下标标签
                .replaceAll(":(.*?):", "")// 移除特殊表情标签
                .replaceAll("```.*", "")// 移除代码标签
                .replaceAll("`(.*?)`", " $1 ")// 移除行内代码标签
                .replaceAll("---", "")// 移除分割线
                .replaceAll("- " , "")// 移除无序列表符号
                .replaceAll("\\d+\\.\\s+", "")// 移除有序列表符号
                .replaceAll("#+\\s+", "")// 移除标题符号，不止6个#
                .replaceAll("\\|\\s:", "")// 移除表格符号1
                .replaceAll(" \\|\\s", "")// 移除表格符号2
                .replaceAll("\\s\\|", "")// 移除表格符号3
                .replaceAll(">\\s", "")// 移除引用符号
                .replaceAll(":::.*", "")// 移除特殊文本符号
                .replaceAll("[\\r\\n]+", " ")// 移除多余的换行符
                .replaceAll("\\s{2,}", " ")// 移除多余的空格
                .trim()// 去除首尾空格
                ;
    }

    // 截取文本，保留关键词周围的内容
    public static String truncateTextAroundKeyword(String text, String keyword, int maxLength) {
        if (text == null || keyword == null || text.isEmpty() || keyword.isEmpty()) {
            return text.length() >= maxLength ? text.substring(0, maxLength-3)+"..." : text;
        }

        String lowerText = text.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();

        int keywordIndex = lowerText.indexOf(lowerKeyword);

        // 统计出现次数，增加最大内容长度
        String spanStartLabel = "<hk>";
        String spanEndLabel = "</hk>";
        int count = 0;
        if (text.length() >= spanEndLabel.length()) {
            int index = 0;
            while (true) {
                index = text.indexOf(spanEndLabel, index);
                if (index == -1) {
                    break;
                }
                count++;
                index += spanEndLabel.length();
            }
            maxLength += count * (spanStartLabel.length() + spanEndLabel.length());
        }

        if (keywordIndex == -1) {
            return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
        }

        int halfLength = maxLength / 2;
        int start = Math.max(0, keywordIndex - halfLength);
        int end = Math.min(text.length(), keywordIndex + keyword.length() + halfLength);

        if (end - start > maxLength) {
            end = start + maxLength;
        }

        StringBuilder result = new StringBuilder();

        if (start > 0) {
            result.append("...");
        }

        result.append(text.substring(start, end));

        if (end < text.length()) {
            result.append("...");
        }

        return result.toString();
    }

    public static String getOriginalTextByReflection(Search search, String fieldName) {
        try {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getter = search.getClass().getMethod(methodName);
            Object value = getter.invoke(search);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setFieldByReflection(Search search, String fieldName, String value) {
        try {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method setter = search.getClass().getMethod(methodName, String.class);
            setter.invoke(search, value);
        } catch (Exception e) {
            // 如果字段不存在或不是String类型，忽略
        }
    }
}
