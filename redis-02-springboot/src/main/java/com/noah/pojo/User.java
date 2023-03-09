package com.noah.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author noah
 * @version 1.0
 * @Description TODO
 * Create by 2023/1/31 16:19
 */
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private String name;
    private int age;
}
