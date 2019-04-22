package com.example.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther: mzc
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date created;
    private Date modified;
}