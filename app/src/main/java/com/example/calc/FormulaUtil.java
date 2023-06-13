package com.example.calc;

import android.icu.math.BigDecimal;

import java.util.Stack;

/**
 * @author 30415
 */
public class FormulaUtil {

    /*
    大致思路是利用栈的出栈入栈压栈来处理一个运算式的先后级排序，进行相映的运算.
     */

    /**
     * 数字栈：用于存储表达式中的各个数字
     * 符号栈：用于存储运算符和括号
     */
    private Stack<BigDecimal> numberStack = null;
    private Stack<Character> symbolStack = null;
    /**
     * 除法时出现循环小数保留精度
     */
    private static final int SCALE = 15;

    public FormulaUtil() {
        super();
    }

    public BigDecimal calculate(String numStr) {
        //判断算数表示是否结束，判断结尾有木有=号
        //equals方法：值的比较
        //charAt方法：检索方法
        if (numStr.length() > 0
                && !"=".equals(numStr.charAt(numStr.length() - 1) + "")) {
            numStr += "=";
        }
        //检查表达式是否是正确的！利用Standard方法(自定义)
        if (!isStandard(numStr)) {
            return null;
        }

        // 初始化栈
        if (numberStack == null) {
            numberStack = new Stack<>();
        }
        numberStack.clear();
        if (symbolStack == null) {
            symbolStack = new Stack<>();
        }
        symbolStack.clear();

        //创建一个StringBuilder，用来放多位的数字
        StringBuilder temp = new StringBuilder();

        // 从表达式的第一个字符开始处理
        for (int i = 0; i < numStr.length(); i++) {
            // 获取一个字符
            char ch = numStr.charAt(i);
            // 若当前字符是数字
            if (isNumber(ch)) {
                // 加入到数字缓存中
                temp.append(ch);
            } else { // 非数字的情况
                // 将数字缓存转为字符串
                String tempStr = temp.toString();
                if (!tempStr.isEmpty()) {
                    BigDecimal num = new BigDecimal(tempStr);
                    // 将数字压栈
                    numberStack.push(num);
                    // 重置数字缓存
                    temp = new StringBuilder();
                }

                // 判断运算符的优先级，若当前优先级低于栈顶的优先级，则先把计算前面计算出来
                while (!comparePri(ch) && !symbolStack.empty()) {
                    // 出栈，取出数字，后进先出
                    BigDecimal b = numberStack.pop();
                    BigDecimal a = numberStack.pop();
                    // 取出运算符进行相应运算，并把结果压栈进行下一次运算
                    switch (symbolStack.pop()) {
                        case '+':
                            numberStack.push(a.add(b));
                            break;
                        case '-':
                            numberStack.push(a.subtract(b));
                            break;
                        case '×':
                            numberStack.push(a.multiply(b));
                            break;
                        case '÷':
                            numberStack.push(a.divide(b, SCALE, BigDecimal.ROUND_HALF_UP));
                            break;
                        case '^':
                            numberStack.push(BigDecimal.valueOf(Math.pow(a.doubleValue(), b.doubleValue()))
                                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP));
                            break;
                        default:
                            break;
                    }
                } // while循环结束

                if (ch != '=') {
                    // 符号入栈
                    symbolStack.push(ch);
                    // 去括号
                    if (ch == ')') {
                        symbolStack.pop();
                        symbolStack.pop();
                    }
                }
            }
        } // for循环结束

        // 返回计算结果
        return numberStack.pop();
    }


    /**
     * =================================检查算数表达式是否合格======================================
     */
    private boolean isStandard(String numStr) {
        // 表达式不能为空
        if (numStr == null || numStr.isEmpty()) {
            return false;
        }
        // 用来保存括号，检查左右括号是否匹配
        Stack<Character> stack = new Stack<>();
        // 用来标记'='符号是否存在多个
        boolean b = false;
        for (int i = 0; i < numStr.length(); i++) {
            char n = numStr.charAt(i);
            // 判断字符是否合法
            if (!(isNumber(n) || "(".equals(n + "") || ")".equals(n + "")
                    || "+".equals(n + "") || "-".equals(n + "")
                    || "×".equals(n + "") || "÷".equals(n + "") || "=".equals(n + "") || "^".equals(n + ""))) {
                return false;
            }
            // 将左括号压栈，用来给后面的右括号进行匹配
            if ("(".equals(n + "")) {
                stack.push(n);
            }
            // 匹配括号
            if (")".equals(n + "")) {
                // 括号是否匹配
                if (stack.isEmpty() || !"(".equals((char) stack.pop() + "")) {
                    return false;
                }
            }
            // 检查是否有多个'='号
            if ("=".equals(n + "")) {
                if (b) {
                    return false;
                }
                b = true;
            }
        }
        // 可能会有缺少右括号的情况
        if (!stack.isEmpty()) {
            return false;
        }
        // 检查'='号是否不在末尾
        return "=".equals(numStr.charAt(numStr.length() - 1) + "");
    }

    /**
     * =================================判断是否是0-9的数字========================================
     */
    private boolean isNumber(char num) {
        return (num >= '0' && num <= '9') || num == '.';
    }

    /**
     * ==============比较优先级，如果当前运算符比栈顶元素运算符优先级高则返回true,否则返回false==========
     */
    private boolean comparePri(char symbol) {
        // 空栈返回ture
        if (symbolStack.empty()) {
            return true;
        }

        /*
         1：（
         2:  ^
         3： × ÷
         4： + -
         5：）
         */

        // 查看堆栈顶部的对象
        char top = symbolStack.peek();
        if (top == '(') {
            return true;
        }
        // 比较优先级
        switch (symbol) {
            case '(':
                return true;
            case '^':
                return top == '×' || top == '÷'
                        || top == '+' || top == '-';
            // 优先级比'+'和'-'高
            case '×':
            case '÷':
                return top == '+' || top == '-';
            case '+':
            case '-':
                return false;
            // 优先级最低
            case ')':
                return false;
            // 结束符
            case '=':
                return false;
            default:
                break;
        }
        return true;
    }
}

