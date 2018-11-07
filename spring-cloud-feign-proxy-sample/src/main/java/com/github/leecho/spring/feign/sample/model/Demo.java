package com.github.leecho.spring.feign.sample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liqiu
 * @create 2018/9/21 13:58
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Demo {

	private String name;

	private String data;

}
