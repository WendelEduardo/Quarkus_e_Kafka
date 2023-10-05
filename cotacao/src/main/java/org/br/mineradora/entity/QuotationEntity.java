package org.br.mineradora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "quotation")
@Entity
@Data
@NoArgsConstructor
public class QuotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    @Column(name = "currency_price")
    private BigDecimal currencyPrice;

    @Column(name = "pct_change")
    private String pctChange;

    private String pair;

    public QuotationEntity(Date date, BigDecimal currencyPrice, String pctChange, String pair) {
        this.date = date;
        this.currencyPrice = currencyPrice;
        this.pctChange = pctChange;
        this.pair = pair;
    }
}
