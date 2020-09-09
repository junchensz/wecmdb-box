package com.webank.cmdb.wecmdbbox.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

//@ShellComponent
public class MyCommand {
  //  @ShellMethod("Add two integers together.")
    public int add(int a, int b) {
        return a + b;
    }
}
