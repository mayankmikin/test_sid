package com.example.test.utils;

import lombok.Data;

@Data
public class Validators {

	public static  boolean isValidInput(Integer option,Integer filecount)
	{
		
		return option > -1 && option <filecount;
	}
}
