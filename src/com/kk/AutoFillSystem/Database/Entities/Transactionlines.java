/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yi
 */
@Entity
@Table(name = "transactionlines")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transactionlines.findAll", query = "SELECT t FROM Transactionlines t"),
    @NamedQuery(name = "Transactionlines.findById", query = "SELECT t FROM Transactionlines t WHERE t.id = :id"),
    @NamedQuery(name = "Transactionlines.findByQuantity", query = "SELECT t FROM Transactionlines t WHERE t.quantity = :quantity")})
public class Transactionlines implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "prod_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Products prodId;
    @JoinColumn(name = "sell_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Transactions sellId;

    public Transactionlines() {
    }

    public Transactionlines(Integer id) {
        this.id = id;
    }

    public Transactionlines(Integer id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Products getProdId() {
        return prodId;
    }

    public void setProdId(Products prodId) {
        this.prodId = prodId;
    }

    public Transactions getSellId() {
        return sellId;
    }

    public void setSellId(Transactions sellId) {
        this.sellId = sellId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transactionlines)) {
            return false;
        }
        Transactionlines other = (Transactionlines) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Transactionlines[ id=" + id + " ]";
    }
    
}
