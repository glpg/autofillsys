/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Entities;

import java.io.Serializable;
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
@Table(name = "ustrkings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ustrkings.findAll", query = "SELECT u FROM Ustrkings u"),
    @NamedQuery(name = "Ustrkings.findById", query = "SELECT u FROM Ustrkings u WHERE u.id = :id"),
    @NamedQuery(name = "Ustrkings.findByTrkingNum", query = "SELECT u FROM Ustrkings u WHERE u.trkingNum = :trkingNum"),
    @NamedQuery(name = "Ustrkings.findByShipDate", query = "SELECT u FROM Ustrkings u WHERE u.shipDate = :shipDate"),
    @NamedQuery(name = "Ustrkings.findByDelivered", query = "SELECT u FROM Ustrkings u WHERE u.delivered = :delivered")})
public class Ustrkings implements Serializable {
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
    @Column(name = "delivered")
    private boolean delivered;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ustrkingId")
    private Collection<Trklines> trklinesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ustrkingId")
    private Collection<Usanduscntrkings> usanduscntrkingsCollection;
    @JoinColumn(name = "carrier_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Carriers carrierId;
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Addresses addressId;
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orders orderId;

    public Ustrkings() {
    }

    public Ustrkings(Integer id) {
        this.id = id;
    }

    public Ustrkings(Integer id, String trkingNum, Date shipDate, boolean delivered) {
        this.id = id;
        this.trkingNum = trkingNum;
        this.shipDate = shipDate;
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

    public boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @XmlTransient
    public Collection<Trklines> getTrklinesCollection() {
        return trklinesCollection;
    }

    public void setTrklinesCollection(Collection<Trklines> trklinesCollection) {
        this.trklinesCollection = trklinesCollection;
    }

    @XmlTransient
    public Collection<Usanduscntrkings> getUsanduscntrkingsCollection() {
        return usanduscntrkingsCollection;
    }

    public void setUsanduscntrkingsCollection(Collection<Usanduscntrkings> usanduscntrkingsCollection) {
        this.usanduscntrkingsCollection = usanduscntrkingsCollection;
    }

    public Carriers getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Carriers carrierId) {
        this.carrierId = carrierId;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
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
        if (!(object instanceof Ustrkings)) {
            return false;
        }
        Ustrkings other = (Ustrkings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Ustrkings[ id=" + id + " ]";
    }
    
}
