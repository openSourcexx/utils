package com.example;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class JSONUtil {
    public static void main(String[] args) {
        String str = "{\"0\":{\"errmsg\":\"OK\",\"errno\":\"570\",\"result\":{\"DATASOURCE\":\"3\",\"IMP_FILE\":{\"tradeResult\":\"00000000000\",\"sysId\":\"ccs\",\"busiNo\":\"2507853442051041045\",\"tradeDesc\":\"交易成功!\",\"fileId\":\"0f2c5bb2d84b41b4802af629df991052\"},\"RESULT\":\"00\",\"VIS_BRNO\":\"北京市东城区公安局\"},\"service\":\"CustomerIDCheck\"}}";
        String str2 = "{\"ApplicantEmail\":null,\"productid\":\"WSTAR\",\"ApplicantHomeAddress\":null,\"zxInCustomerPhoneNumber\":\"18500180038\",\"bdCustomerBankCard\":\"6217718380432019\",\"zxNQueryNo\":\"181210bzcriicr1536226238899396682\",\"productName\":\"百分贷-产品\",\"applyAmount\":\"100000.00\",\"firApplyTime\":null,\"dailyRate\":null,\"CompanyAddress\":null,\"CompanyName\":null,\"loanDuration\":null,\"applydate\":null,\"sysPrcid2FileId\":null,\"credit_limit\":null,\"loanPurpose\":null,\"ApplicantPhone\":\"18500180038\",\"queryNo\":null,\"sex\":\"0\",\"bdCustomerPhoneNumber\":\"18500180038\",\"sessionId\":\"20181227140840_7915545876558904461\",\"userId\":\"100012271001\",\"repaymentMode\":null,\"loanAmount\":null,\"certNo\":\"110116198602125838\",\"sysPrcid1FileId\":null,\"ReceiveCard\":null,\"available_limit\":null,\"initialAmount\":\"100000.00\",\"name\":\"嵇澜\",\"bid\":null,\"age\":32,\"contacts\":[]}";
        System.out.println(formatJson(str));
        System.out.println(parser(str2,Map.class));
    }
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\'){
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    public static String toJSONString(Object o) {
        return JSONObject.toJSONString(o);
    }

    public static Map<String,Object> parser(String src, Class<Map> clazz) {
        return JSONObject.parseObject(src,clazz);
    }
}
