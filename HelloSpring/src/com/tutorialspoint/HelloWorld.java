/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tutorialspoint;

/**
 *
 * @author owner
 */
public class HelloWorld {
    
    
    private String message;
    
    public void setMessage(String message){
        this.message = message;
    }
    public void getMessage(){
        System.out.println("Your message is "+message);
    }
    
}
