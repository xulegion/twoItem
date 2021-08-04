package com.yc.utils;

public class AliPayConfig {
    // [沙箱环境]应用ID,您的APPID，收款账号既是你的APPID对应支付宝账号
    public static String app_id = "2021000117690148";
    // [沙箱环境]商户私钥，你的PKCS8格式RSA2私钥
    public static String merchant_private_key ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCS94atH/izHEmVax/dSxQ/IjbMkh0YLzvxz+1IH6aYWH27JVZVkaDq5fCNiiJqKAUJBAoJlmeSRFxyYeT2dGPfv04W4gLLl9uHuGI/j5N+6JZ/MwNARGwrv331blh1x3QLpwFfXJfrSe3W7FUEIIK7OtGua+Lx8vc/quoPto08ZJkJ924fA4cI8g+ahU7cwJzB6XEfmkyig33V0LdJsMSdcSRtvle9qWAlT5AUzL7ac3TQmuVccKEjgPWGs/nMFvsqBr4X96X4pnkgTZgk3NnD6r20IS885ZTQwBgLeJg9Tm4mpbwoiguRxoiED2Bj5qnAqj63PjTSuxUNHXBKXALRAgMBAAECggEAKUaHdnvyNyuMpwBNFLOPXiPXzCCFN1OcvE8vTwBEo6jGtxbHZoTd8trdcoMHI1VWBLAzuXmaYQP+CsnfqjAKFL7/yYF0U4W0zCtGvJ8wR4Y/Lj4/fX85+hdv4sw43+tCdjtTMVsp95TtzVzWi7AtxzO/zALU3CrmseTok70rcXhHLBqrPB+dzT6W2XkHNnH8JdgJzcYs9QB2/Jk2TNh707Dgj2D54DDXUtjLcNu5v7XpoH60J0dbAUWJYvhpNedmvl3m5upovvKMniZvLbZgr0/lWRZPdB/Ry5+WFaFoFIPc69W2Xy9KU+mWbguktcYkTuqtQjKfzD9XzarWabzKoQKBgQD3TIHHafOvreBPQt6+oEb9u4uvZD+y8yzmOk6+uXaXpB1FF1jJmKoVyTAX3WiWd8rr+jIDxBuv9LeTvIRoNsaPRHSZJiJH6TQOSZ6g07UCAmnnMIm5oafaQhyYiqcw/RGOKnCGiE5qLovd7A+FC0UwSeNxrgkd1v9daLnNdkWphQKBgQCYI0zE9g7zvrRXKlaitswzoDEWaNI0DZABtu65FYsWBcCFrf+6Pv8E4JX+cDSGzd6s91cnHCnZgHMq+7PyhWwQBTe4q2Hud6jHIDBvkWQsIRMQFOJ5cGm8gxprfF/UmcgUQe+xHvjSGzZLWneZO7ePXnqGM6FFLN68/yzOvlhv3QKBgQDgw5OM2DqYqLMftwpWPbxoLwYlUjsm/SgI1GPvqwcjihDkU/TpvayRAnuIpT7Cy3wOgCbnD12Ozh/v0b6SLX7IhhJng6ZSr50Vg0n+qIxymBkC/DWhF1FE6SOubQ7KUqibCu2dCNQkY8vR1xS/+l9XJUDoeKJmkZslneKJ4H4uNQKBgHD21QvygEuwMIwAXe/Xt2wl6AzxrBMGdwUu5bb8LhgeEUOxXQ7Xs9fVQsp4Wig2OL9JsKbTnKdpXxptZsIPG5wRo7w1VTQSGXoxhoVw+WZi8JqiRz9QntJgrn9dDHL59LxF8Uoc6zhnCDuPYvqegr+rsWJwd7C7jm/bMVQZteTpAoGBALL7i2Spizcak9oo1lh5WjKvUlItcJC0KG+KQf0xgKiM70am+3tL0MBzyFkj/CvhS6PT6FT0HTFawhk3x/ZPJFgWzodiOYY4nAd7qc+lZ7jFuP12IkejOgL+B8mbTx68D0vMp277likYZT84O25d+95XWZcmCNk1YZgkY12lK5sm";
    // [沙箱环境]支付宝公钥
    public static String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlYYrFcVNW+lQ0JMdyRRvgklEEnfTTWNjMxHOzW8IDZzn5kD0XaUY+GbZaB0loDBAdqAFNaJtYNKzJzlkecPQRpg/xKAgK9S1zrSwtq9Yajjcsoxn8mILl7pY8cDMH1tA3qDxEfPDFzLl+Quj9iw1osHUA3/tRF2tjvlFqYBmEzD2MzR4e8tLf2HbsLB1x9UTF54Gc7fiUSP6+Uw5DvH87ChdulcOjDud9bDwMlCF0ekfo+lCzLjhbVcErev2VWBe8yC2wIB1xefK+jj5+NOEKQL5ci2/PLjZSTPb/qjo3EQ30bTOhuQDkOxhkVoJ8L+2/8sZprUsSxMYoB87bdjvIwIDAQAB";

    // [沙箱环境]服务器异步通知页面路径
//    public static String notify_url="http://4n1c310178.qicp.vip/twoItem/aliPay.action?op=Opnotify";
    public static String notify_url="http://39.103.237.140/twoItem30/aliPay.action?op=Opnotify";


    // [沙箱环境]页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url ="http://39.103.237.140/twoItem30/index.html";   //临时
//    public static String return_url ="http://4n1c310178.qicp.vip/twoItem/shopcart.html";

    // [沙箱环境]
    public static String gatewayUrl ="https://openapi.alipaydev.com/gateway.do";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";
}
