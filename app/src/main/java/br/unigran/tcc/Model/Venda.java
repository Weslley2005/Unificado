package br.unigran.tcc.Model;

import java.util.Date;

public class Venda {
     private Integer id;
     private Date data;
     private Float total;
     private Float desconto;
     private Float procoTotal;

     public Integer getId() {
          return id;
     }

     public void setId(Integer id) {
          this.id = id;
     }

     public Date getData() {
          return data;
     }

     public void setData(Date data) {
          this.data = data;
     }

     public Float getTotal() {
          return total;
     }

     public void setTotal(Float total) {
          this.total = total;
     }

     public Float getDesconto() {
          return desconto;
     }

     public void setDesconto(Float desconto) {
          this.desconto = desconto;
     }

     public Float getProcoTotal() {
          return procoTotal;
     }

     public void setProcoTotal(Float procoTotal) {
          this.procoTotal = procoTotal;
     }
}
