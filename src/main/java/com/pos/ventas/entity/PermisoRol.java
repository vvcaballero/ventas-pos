
package com.pos.ventas.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "permisos_rol")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermisoRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rol", nullable = false, unique = true, length = 50)
    private String rol;

    // DASHBOARD
    private Boolean dashboardVer;

    // PUNTO DE VENTA
    private Boolean puntoVentaVer;

    // VENTAS
    private Boolean ventasVer;
    private Boolean ventasCrear;
    private Boolean ventasEditar;
    private Boolean ventasEliminar;

    // PRODUCTOS
    private Boolean productosVer;
    private Boolean productosCrear;
    private Boolean productosEditar;
    private Boolean productosEliminar;

    // INVENTARIO
    private Boolean inventarioVer;
    private Boolean inventarioCrear;
    private Boolean inventarioEditar;

    // CLIENTES
    private Boolean clientesVer;
    private Boolean clientesCrear;
    private Boolean clientesEditar;
    private Boolean clientesEliminar;

    // REPORTES
    private Boolean reportesVer;

    // USUARIOS
    private Boolean usuariosVer;
    private Boolean usuariosCrear;
    private Boolean usuariosEditar;
    private Boolean usuariosEliminar;

    // CONFIGURACIÓN
    private Boolean configuracionVer;
    private Boolean configuracionEditar;
}
