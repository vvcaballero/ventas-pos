
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
@NoArgsConstructor
@AllArgsConstructor
public class PermisoRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rol", nullable = false, unique = true, length = 50)
    private String rol;

    @Column(name = "dashboard_ver", nullable = false)
    private Boolean dashboardVer = false;

    @Column(name = "punto_venta_ver", nullable = false)
    private Boolean puntoVentaVer = false;

    @Column(name = "ventas_ver", nullable = false)
    private Boolean ventasVer = false;

    @Column(name = "ventas_crear", nullable = false)
    private Boolean ventasCrear = false;

    @Column(name = "ventas_editar", nullable = false)
    private Boolean ventasEditar = false;

    @Column(name = "ventas_eliminar", nullable = false)
    private Boolean ventasEliminar = false;

    @Column(name = "productos_ver", nullable = false)
    private Boolean productosVer = false;

    @Column(name = "productos_crear", nullable = false)
    private Boolean productosCrear = false;

    @Column(name = "productos_editar", nullable = false)
    private Boolean productosEditar = false;

    @Column(name = "productos_eliminar", nullable = false)
    private Boolean productosEliminar = false;

    @Column(name = "inventario_ver", nullable = false)
    private Boolean inventarioVer = false;

    @Column(name = "inventario_crear", nullable = false)
    private Boolean inventarioCrear = false;

    @Column(name = "inventario_editar", nullable = false)
    private Boolean inventarioEditar = false;

    @Column(name = "clientes_ver", nullable = false)
    private Boolean clientesVer = false;

    @Column(name = "clientes_crear", nullable = false)
    private Boolean clientesCrear = false;

    @Column(name = "clientes_editar", nullable = false)
    private Boolean clientesEditar = false;

    @Column(name = "clientes_eliminar", nullable = false)
    private Boolean clientesEliminar = false;

    @Column(name = "reportes_ver", nullable = false)
    private Boolean reportesVer = false;

    @Column(name = "usuarios_ver", nullable = false)
    private Boolean usuariosVer = false;

    @Column(name = "usuarios_crear", nullable = false)
    private Boolean usuariosCrear = false;

    @Column(name = "usuarios_editar", nullable = false)
    private Boolean usuariosEditar = false;

    @Column(name = "usuarios_eliminar", nullable = false)
    private Boolean usuariosEliminar = false;

    @Column(name = "configuracion_ver", nullable = false)
    private Boolean configuracionVer = false;

    @Column(name = "configuracion_editar", nullable = false)
    private Boolean configuracionEditar = false;
}
