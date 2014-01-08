package com.example.jsonparseutils;

import android.graphics.Bitmap;

public class Person {
	private String name;
	private int age;
	private String address;
	private Bitmap headImg;

	public Person(String name, int age, String address, Bitmap headImg) {
		this.name = name;
		this.age = age;
		this.address = address;
		this.headImg = headImg;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public String getAddress() {
		return address;
	}

	public Bitmap getHeadImg() {
		return headImg;
	}

}
