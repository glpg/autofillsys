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
@Table(name = "trklines")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trklines.findAll", query = "SELECT t FROM Trklines t"),
    @NamedQuery(name = "Trklines.findById", query = "SELECT t FROM Trklines t WHERE t.id = :id"),
    @NamedQuery(name = "Trklines.findByQuantity", query = "SELECT t FROM Trklines t WHERE t.quantity = :quantity")})
public class Trklines implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Products productId;
    @JoinColumn(name = "ustrking_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Ustrkings ustrkingId;

    public Trklines() {
    }

    public Trklines(Integer id) {
        this.id = id;
    }

    public Trklines(Integer id, int quantity) {
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

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public Ustrkings getUstrkingId() {
        return ustrkingId;
    }

    public void setUstrkingId(Ustrkings ustrkingId) {
        this.ustrkingId = ustrkingId;
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
        if (!(object instanceof Trklines)) {
            return false;
        }
        Trklines other = (Trklines) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Trklines[ id=" + id + " ]";
    }
    
}
