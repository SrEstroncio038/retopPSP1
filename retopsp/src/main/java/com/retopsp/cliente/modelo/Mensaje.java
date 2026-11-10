package com.retopsp.cliente.modelo;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;
    private String emisor;
    private String receptor;
    private boolean privado;
    private String mensaje;
    private boolean notificacion;

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public boolean isPrivado() {
        return privado;
    }

    public void setPrivado(boolean privado) {
        this.privado = privado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public boolean isNotificacion() {
        return notificacion;
    }

    public void setNotificacion(boolean notificacion) {
        this.notificacion = notificacion;
    }

    public Mensaje(String emisor, String receptor, boolean privado, String mensaje, boolean notificacion) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.privado = privado;
        this.mensaje = mensaje;
        this.notificacion = notificacion;
    }
    

}
