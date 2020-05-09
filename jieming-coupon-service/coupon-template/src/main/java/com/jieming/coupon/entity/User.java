package com.jieming.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name= "account",nullable = false)
    private String account;

    @Column(name= "password",nullable = false)
    private String password;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "level",nullable = false)
    private short level;
}
