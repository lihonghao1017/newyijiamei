package com.sucetech.yijiamei.bean;

import java.util.ArrayList;
import java.util.List;

public class ErrorDetails {
    public String error;
    public List<ErrorInfo> errors = new ArrayList<>();
    public String message;
    public String path;
    public String timestamp;
}
