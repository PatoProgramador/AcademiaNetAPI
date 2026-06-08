package com.academiaNetAPI.demo.entity;

import com.academiaNetAPI.demo.enums.SubscriptionPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "companies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_companies_nit", columnNames = "nit"),
        @UniqueConstraint(name = "uk_companies_domain", columnNames = "domain")
})
@SQLDelete(sql = "UPDATE companies SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Company extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 30)
    private String nit;

    @Column(length = 100)
    private String domain;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(length = 3)
    private String currency = "COP";

    @Column(length = 2)
    private String country = "CO";

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", length = 20)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.BASIC;

    @Column(nullable = false)
    private boolean active = true;
}
