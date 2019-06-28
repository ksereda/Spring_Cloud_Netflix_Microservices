package com.example.service_statistics.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
@Table(name="table_employees")
public class EmployeeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    public String name;

    @Column(name="lastName", nullable = false)
    public String lastName;

    @Column(name="age", nullable=false, length=200)
    public Long age;

}
