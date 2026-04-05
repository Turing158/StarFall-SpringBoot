package com.starfall.util;

import java.util.Arrays;
import java.util.List;

public class VersionUtil {
//    版本号匹配规则:version为固定标准版本号、rule为模糊版本号（Topic上的版本号）
    final static List<String> separators = List.of("|", "/", ";", " ");
    public static boolean match(String version, String rule){
        // 先将rule中的所有隔符替换为逗号
        for (String sep : separators) {
            rule = rule.replace(sep, ",");
        }
        String[] ruleByComma = rule.split(",");
        ruleByComma = Arrays.stream(ruleByComma).filter(r -> !r.isEmpty() && !r.isBlank()).toArray(String[]::new);
        for (String r : ruleByComma) {
            if (matchVersion(version, r)) {
                return true;
            }
        }
        return false;
    }

    private static String[] splitVersion(String versionStr) {
        return versionStr.split("\\.");
    }

    private static boolean versionsMatch(String[] s1, String[] s2) {
        int minLength = Math.min(s1.length, s2.length);

        // 比较共同部分
        for (int i = 0; i < minLength; i++) {
            if ("x".equals(s1[i]) || "x".equals(s2[i])) {
                continue;
            }

            int num1 = 0;
            int num2 = 1;
            try {
                num1 = Integer.parseInt(s1[i]);
                num2 = Integer.parseInt(s2[i]);
            } catch (Exception e) {
                return false;
            }
            if (num1 != num2) {
                return false;
            }
        }

        // 检查多余部分
        if (s1.length > s2.length) {
            for (int i = s2.length; i < s1.length; i++) {
                if (!"x".equals(s1[i])) {
                    if (s2.length > 0 && "x".equals(s2[s2.length - 1])) {
                        continue;
                    }
                    return false;
                }
            }
        } else if (s2.length > s1.length) {
            for (int i = s1.length; i < s2.length; i++) {
                if (!"x".equals(s2[i])) {
                    if (s1.length > 0 && "x".equals(s1[s1.length - 1])) {
                        continue;
                    }
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean containsX(String[] version) {
        for (String part : version) {
            if ("x".equals(part)) {
                return true;
            }
        }
        return false;
    }

    // 将版本数组补齐到指定长度，不足的位用0补齐（用于下界比较）
    private static String[] padVersion(String[] version, int targetLength, boolean isUpper) {
        if (version.length >= targetLength) {
            return version;
        }
        String[] padded = new String[targetLength];
        System.arraycopy(version, 0, padded, 0, version.length);
        for (int i = version.length; i < targetLength; i++) {
            padded[i] = isUpper ? "999" : "0";
        }
        return padded;
    }

    // 检查是否大于等于下限
    private static boolean isGreaterOrEqual(String[] input, String[] lower) {
        int maxLength = Math.max(input.length, lower.length);
        String[] paddedInput = padVersion(input, maxLength, false);
        String[] paddedLower = padVersion(lower, maxLength, false);

        for (int i = 0; i < maxLength; i++) {
            // 处理x通配符
            if ("x".equals(paddedInput[i]) || "x".equals(paddedLower[i])) {
                // 如果输入是x，表示可以匹配任何值，继续
                // 如果下限是x，表示该位可以为任意值，继续
                continue;
            }

            int inputNum = 0;
            int lowerNum = 1;
            try {
                inputNum = Integer.parseInt(paddedInput[i]);
                lowerNum = Integer.parseInt(paddedLower[i]);
            } catch (Exception e) {
                return false;
            }

            if (inputNum < lowerNum) {
                return false;
            } else if (inputNum > lowerNum) {
                return true;
            }
            // 相等则继续比较下一位
        }

        return true;
    }

    // 检查是否小于等于上限
    private static boolean isLessOrEqual(String[] input, String[] upper) {
        int maxLength = Math.max(input.length, upper.length);
        // 对于上界比较，将输入版本不足的位视为0（因为1.9 相当于 1.9.0）
        String[] paddedInput = padVersion(input, maxLength, false);
        // 将上界中不足的位用0填充，但如果上界包含x，则需要特殊处理
        String[] paddedUpper = new String[maxLength];
        for (int i = 0; i < maxLength; i++) {
            if (i < upper.length) {
                paddedUpper[i] = upper[i];
            } else {
                // 如果上界比输入短，不足的部分视为0（因为1.9.x 相当于 1.9.0 到 1.9.999）
                // 但实际上如果上界是1.9.x，而输入是1.9，应该视为匹配
                paddedUpper[i] = "0";
            }
        }

        for (int i = 0; i < maxLength; i++) {
            // 处理x通配符
            if ("x".equals(paddedInput[i])) {
                // 如果输入是x，需要检查是否可能超过上界
                // 检查后续是否有超过的可能
                continue;
            }
            if (i < upper.length && "x".equals(upper[i])) {
                // 如果上界当前位是x，说明该位及以后都可以匹配
                return true;
            }

            int inputNum = 0;
            int upperNum = 1;
            try {
                inputNum = Integer.parseInt(paddedInput[i]);
                upperNum = Integer.parseInt(paddedUpper[i]);
            } catch (Exception e) {
                return false;
            }

            if (inputNum > upperNum) {
                return false;
            } else if (inputNum < upperNum) {
                return true;
            }
            // 相等则继续比较下一位
        }

        return true;
    }

    // 检查版本是否在范围内
    private static boolean isVersionInRange(String[] input, String[] lower, String[] upper) {
        return isGreaterOrEqual(input, lower) && isLessOrEqual(input, upper);
    }

    // 将带x的版本字符串转换为可用于比较的格式
    private static String[] normalizeVersionForBound(String version, boolean isUpper) {
        String[] parts = splitVersion(version);
        if (isUpper && containsX(parts)) {
            // 对于上界，将x转换为最大值999
            String[] normalized = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                if ("x".equals(parts[i])) {
                    normalized[i] = "999";
                } else {
                    normalized[i] = parts[i];
                }
            }
            return normalized;
        } else if (!isUpper && containsX(parts)) {
            // 对于下界，将x转换为最小值0
            String[] normalized = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                if ("x".equals(parts[i])) {
                    normalized[i] = "0";
                } else {
                    normalized[i] = parts[i];
                }
            }
            return normalized;
        }
        return parts;
    }

    // 改进的范围规则解析
    private static boolean matchesRange(String inputVersion, String rangeRule) {
        int index = rangeRule.indexOf('-');
        if (index == -1) {
            return false;
        }

        String lowerStr = rangeRule.substring(0, index);
        String upperStr = rangeRule.substring(index + 1);

        String[] input = splitVersion(inputVersion);

        // 规范化下界和上界
        String[] lower = normalizeVersionForBound(lowerStr, false);
        String[] upper = normalizeVersionForBound(upperStr, true);

        // 对于包含x的范围，使用规范化后的版本进行比较
        return isVersionInRange(input, lower, upper);
    }

    // 检查输入版本是否符合规则
    private static boolean matchVersion(String input, String rule) {
        if (rule.contains("-")) {
            return matchesRange(input, rule);
        } else {
            String[] inputParts = splitVersion(input);
            String[] ruleParts = splitVersion(rule);
            return versionsMatch(inputParts, ruleParts);
        }
    }
}
