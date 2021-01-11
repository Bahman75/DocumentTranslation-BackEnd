package com.document.documentTranslator.enums;


import com.document.documentTranslator.util.Validator;

public enum ErrorMessage {
    EMPTY_PARAMETER(1, "EmptyPARAMETER", "وارد کردن %s الزامی است", "%s is empty"),
    NOT_EMPTY_PARAMETER(2, "NOtEmptyPARAMETER", "%s را نباید وارد کنید", "%s is not empty"),
    NOT_FOUND(3, "NOT_FOUND", "با اطلاعات ارسالی، %s یافت نشد", "%s not found"),
    INVALID_PARAMETER(4, "InvalidPARAMETER", "%s به درستی وارد نشده است", "%s is invalid"),
    INVALID_DATE_FORMAT(5, "InvalidDateFormat", "فرمت تاریخ به درستی وارد نشده است", "invalid date format"),
    USER_ALREADY_EXISTS(6, "userAlreadyExists", "نام کاربری وارد شده تکراری است", "user already exists"),
    INVALID_INPUT(7, "InvalidInput", "ورودی به درستی وارد نشده است", "invalid input"),
    NOT_ADMIN(8, "notAdmin", "کاربر وارد شده مترجم نیست", "not admin"),
    ACCESS_DENIED(9, "accessDenied", "دسترسی محدود", "access denied"),
    ;

    private int code;
    private String messageFarsi;
    private String name;
    private String logMessage;

    private ErrorMessage(int code, String name, String messageFarsi, String logMessage) {

        this.code = code;
        this.name = name;
        this.messageFarsi = messageFarsi;
        this.logMessage = logMessage;
    }

    public String getLogMessage(Object... parameters) {

        return String.format(logMessage, parameters);
    }

    public int getCode() {

        return code;
    }

    public String getFarsiMessage(Object... parameters) {

        String result = messageFarsi;
        if (parameters != null && parameters.length > 0)
            result = String.format(messageFarsi, parameters);

        return result;
    }

    public static ErrorMessage fromName(String name) {

        ErrorMessage errorMessage = null;
        if (Validator.validateRequiredString(name)) {
            for (ErrorMessage item : ErrorMessage.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
        }
        return errorMessage;
    }

    public String getName() {

        return name;
    }

}
