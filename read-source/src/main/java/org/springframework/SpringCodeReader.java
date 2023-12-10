package org.springframework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringCodeReader {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		SimpleBean simpleBean = context.getBean("simpleBean", SimpleBean.class);
		simpleBean.test();
		System.out.println("Hello world!");
	}
}