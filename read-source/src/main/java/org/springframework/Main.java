package org.springframework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		System.out.println(context.getBean("a"));
		System.out.println("Hello world!");
	}
}