/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Yi
 */
@Entity
@Table(name = "ustocntrkings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ustocntrkings.findAll", query = "SELECT u FROM Ustocntrkings u"),
    @NamedQuery(name = "Ustocntrkings.findById", query = "SELECT u FROM Ustocntrkings u WHERE u.id = :id"),
    @NamedQuery(name = "Ustocntrkings.findByTrkingNum", query = "SELECT u FROM Ustocntrkings u WHERE u.trkingNum = :trkingNum"),
    @NamedQuery(name = "Ustocntrkings.findByShipDate", query = "SELECT u FROM Ustocntrkings u WHERE u.shipDate = :shipDate"),
    @NamedQuery(name = "Ustocntrkings.findByWeight", query = "SELECT u FROM Ustocntrkings u WHERE u.weight = :weight"),
    @NamedQuery(name = "Ustocntrkings.findByShippingfee", query = "SELECT u FROM Ustocntrkings u WHERE u.shippingfee = :shippingfee"),
    @NamedQuery(name = "Ustocntrkings.findByDelivered", query = "SELECT u FROM Ustocntrkings u WHERE u.delivered = :delivered")})
public class Ustocntrkings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "trking_num")
    private String trkingNum;
    @Basic(optional = false)
    @Column(name = "ship_date")
    @Temporal(TemporalType.DATE)
    private Date shipDate;
    @Basic(optional = false)
    @Column(name = "weight")
    private int weight;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "shippingfee")
    private BigDecimal shippingfee;
    @Basic(optional = false)
    @Column(name = "delivered")
    private boolean delivered;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ustocntrkingId")
    private Collection<Cntrkings> cntrkingsCollection;
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Addresses addressId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ustocntrkingId")
    private Collection<Usanduscntrkings> usanduscntrkingsCollection;

    public Ustocntrkings() {
    }

    public Ustocntrkings(Integer id) {
        this.id = id;
    }

    public Ustocntrkings(Integer id, String trkingNum, Date shipDate, int weight, BigDecimal shippingfee, boolean delivered) {
        this.id = id;
        this.trkingNum = trkingNum;
        this.shipDate = shipDate;
        this.weight = weight;
        this.shippingfee = shippingfee;
        this.delivered = delivered;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrkingNum() {
        return trkingNum;
    }

    public void setTrkingNum(String trkingNum) {
        this.trkingNum = trkingNum;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public BigDecimal getShippingfee() {
        return shippingfee;
    }

    public void setShippingfee(BigDecimal shippingfee) {
        this.shippingfee = shippingfee;
    }

    public boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @XmlTransient
    public Collection<Cntrkings> getCntrkingsCollection() {
        return cntrkingsCollection;
    }

    public void setCntrkingsCollection(Collection<Cntrkings> cntrkingsCollection) {
        this.cntrkingsCollection = cntrkingsCollection;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    @XmlTransient
    public Collection<Usanduscntrkings> getUsanduscntrkingsCollection() {
        return usanduscntrkingsCollection;
    }

    public void setUsanduscntrkingsCollection(Collection<Usanduscntrkings> usanduscntrkingsCollection) {
        this.usanduscntrkingsCollection = usanduscntrkingsCollection;
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
        if (!(object instanceof Ustocntrkings)) {
            return false;
        }
        Ustocntrkings other = (Ustocntrkings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Ustocntrkings[ id=" + id + " ]";
    }
    
}
