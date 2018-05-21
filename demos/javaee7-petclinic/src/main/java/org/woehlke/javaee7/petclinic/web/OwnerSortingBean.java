package org.woehlke.javaee7.petclinic.web;



import org.richfaces.component.SortOrder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Created by tw on 13.03.14.
 */
@ManagedBean
@SessionScoped
public class OwnerSortingBean implements Serializable {

    private SortOrder sortOrderName = SortOrder.unsorted;
    private SortOrder sortOrderAddress = SortOrder.unsorted;
    private SortOrder sortOrderCity = SortOrder.unsorted;
    private SortOrder sortOrderTelephone = SortOrder.unsorted;

    public void setSortOrderToName(){
        sortOrderAddress = SortOrder.unsorted;
        sortOrderCity = SortOrder.unsorted;
        sortOrderTelephone = SortOrder.unsorted;
        if(sortOrderName==SortOrder.ascending){
            sortOrderName = SortOrder.descending;
        } else {
            sortOrderName = SortOrder.ascending;
        }
    }

    public void setSortOrderToAddress(){
        sortOrderName = SortOrder.unsorted;
        sortOrderCity = SortOrder.unsorted;
        sortOrderTelephone = SortOrder.unsorted;
        if(sortOrderAddress == SortOrder.ascending){
            sortOrderAddress = SortOrder.descending;
        } else {
            sortOrderAddress = SortOrder.ascending;
        }
    }

    public void setSortOrderToCity(){
        sortOrderName = SortOrder.unsorted;
        sortOrderAddress = SortOrder.unsorted;
        sortOrderTelephone = SortOrder.unsorted;
        if(sortOrderCity == SortOrder.ascending){
            sortOrderCity = SortOrder.descending;
        } else {
            sortOrderCity = SortOrder.ascending;
        }
    }

    public void setSortOrderToTelephone(){
        sortOrderName = SortOrder.unsorted;
        sortOrderAddress = SortOrder.unsorted;
        sortOrderCity = SortOrder.unsorted;
        if(sortOrderTelephone == SortOrder.ascending){
            sortOrderTelephone = SortOrder.descending;
        } else {
            sortOrderTelephone = SortOrder.ascending;
        }
    }

    public SortOrder getSortOrderName() {
        return sortOrderName;
    }

    public void setSortOrderName(SortOrder sortOrderName) {
        this.sortOrderName = sortOrderName;
    }

    public SortOrder getSortOrderAddress() {
        return sortOrderAddress;
    }

    public void setSortOrderAddress(SortOrder sortOrderAddress) {
        this.sortOrderAddress = sortOrderAddress;
    }

    public SortOrder getSortOrderCity() {
        return sortOrderCity;
    }

    public void setSortOrderCity(SortOrder sortOrderCity) {
        this.sortOrderCity = sortOrderCity;
    }

    public SortOrder getSortOrderTelephone() {
        return sortOrderTelephone;
    }

    public void setSortOrderTelephone(SortOrder sortOrderTelephone) {
        this.sortOrderTelephone = sortOrderTelephone;
    }
}
