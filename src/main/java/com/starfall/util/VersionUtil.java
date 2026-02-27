package com.starfall.util;

public class VersionUtil {
    public static boolean match(String version, String rule){
        String[] rules = rule.split(" ");
        for (String r :rules) {
            if (matchVersion(version,r)){
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
            // 如果任一方是x，则跳过比较（视为匹配）
            if ("x".equals(s1[i]) || "x".equals(s2[i])) {
                continue;
            }

            // 如果都是具体数字，则必须相等
            int num1 = 0;
            int num2 = 1;
            try{
                num1 = Integer.parseInt(s1[i]);
                num2 = Integer.parseInt(s2[i]);
            }catch(Exception e){

            }
            if (num1 != num2) {
                return false;
            }
        }

        // 检查多余部分
        if (s1.length > s2.length) {
            // v1比v2长，检查v1的多余部分
            for (int i = s2.length; i < s1.length; i++) {
                // 如果v1有多余部分且不是x，则不匹配
                if (!"x".equals(s1[i])) {
                    // 除非v2以x结尾，表示接受任何额外部分
                    if (s2.length > 0 && "x".equals(s2[s2.length - 1])) {
                        continue;
                    }
                    return false;
                }
            }
        } else if (s2.length > s1.length) {
            // v2比v1长，检查v2的多余部分
            for (int i = s1.length; i < s2.length; i++) {
                // 如果v2有多余部分且不是x，则不匹配
                if (!"x".equals(s2[i])) {
                    // 除非v1以x结尾，表示接受任何额外部分
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

    private static boolean isGreaterOrEqual(String[] input, String[] lower) {
        int minLength = Math.min(input.length, lower.length);
        for (int i = 0; i < minLength; i++) {
            if ("x".equals(input[i])) {
                continue;
            }
            if ("x".equals(lower[i])) {
                continue;
            }
            int inputNum = 0;
            int lowerNum = 1;
            try{
                inputNum = Integer.parseInt(input[i]);
                lowerNum = Integer.parseInt(lower[i]);
            }
            catch(Exception e){

            }
            if (inputNum < lowerNum) {
                return false;
            }
            else if (inputNum > lowerNum) {
                return true;
            }
        }
        return true;
    }

    // 检查是否小于等于上限（支持.x输入）
    private static boolean isLessOrEqualInInput(String[] input, String[] upper) {
        // 如果输入包含x通配符，使用宽松匹配
        if (containsX(input)) {
            int minLength = Math.min(input.length, upper.length);
            for (int i = 0; i < minLength; i++) {
                // 如果输入部分是x，视为匹配
                if ("x".equals(input[i])) {
                    continue;
                }
                // 如果上限部分是x，视为匹配
                if ("x".equals(upper[i])) {
                    continue;
                }

                int inputNum = 0;
                int upperNum = 1;
                try{
                    inputNum = Integer.parseInt(input[i]);
                    upperNum = Integer.parseInt(upper[i]);
                }
                catch(Exception e){

                }
                if (inputNum > upperNum) {
                    return false;
                }
                else if (inputNum < upperNum) {
                    return true;
                }
            }
            return true;
        }
        else {
            int minLength = Math.min(input.length, upper.length);
            for (int i = 0; i < minLength; i++) {
                int inputNum = 0;
                int upperNum = 1;
                try{
                    inputNum = Integer.parseInt(input[i]);
                    upperNum = Integer.parseInt(upper[i]);
                }
                catch(Exception e){

                }

                if (inputNum > upperNum) {
                    return false;
                }
                else if (inputNum < upperNum) {
                    return true;
                }
            }

            // 严格检查：输入不能比上限长（除非多余部分为0）
            if (input.length > upper.length) {
                for (int i = upper.length; i < input.length; i++) {
                    if (!"0".equals(input[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    // 检查版本是否在范围内
    private static boolean isVersionInRange(String[] input, String[] lower, String[] upper) {
        return isGreaterOrEqual(input, lower) && isLessOrEqualInInput(input, upper);
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
        String[] lower = splitVersion(lowerStr);
        String[] upper = splitVersion(upperStr);

        return isVersionInRange(input, lower, upper);
    }

    // 检查输入版本是否符合规则
    private static boolean matchVersion(String input, String rule) {
        if (rule.contains("-")) {
            // 处理范围规则
            return matchesRange(input, rule);
        } else {
            // 处理单个规则 - 使用双向宽松匹配
            String[] inputParts = splitVersion(input);
            String[] ruleParts = splitVersion(rule);
            return versionsMatch(inputParts, ruleParts);
        }
    }
}
