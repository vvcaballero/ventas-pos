
package com.pos.ventas.service;

import com.pos.ventas.entity.PermisoRol;
import com.pos.ventas.repository.PermisoRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermisoRolServiceImpl implements PermisoRolService {

    @Autowired
    private PermisoRolRepository permisoRolRepository;

    @Override
    public List<PermisoRol> listarRoles() {
        return permisoRolRepository.findAll();
    }

    @Override
    public PermisoRol obtenerPorRol(String rol) {
        return permisoRolRepository.findByRolIgnoreCase(rol)
                .orElseThrow(() ->
                        new RuntimeException("No existe el rol: " + rol));
    }

    @Override
    @Transactional
    public PermisoRol actualizarPermisos(String rol, PermisoRol p) {

        PermisoRol actual = obtenerPorRol(rol);

        actual.setDashboardVer(p.getDashboardVer());
        actual.setPuntoVentaVer(p.getPuntoVentaVer());

        actual.setVentasVer(p.getVentasVer());
        actual.setVentasCrear(p.getVentasCrear());
        actual.setVentasEditar(p.getVentasEditar());
        actual.setVentasEliminar(p.getVentasEliminar());

        actual.setProductosVer(p.getProductosVer());
        actual.setProductosCrear(p.getProductosCrear());
        actual.setProductosEditar(p.getProductosEditar());
        actual.setProductosEliminar(p.getProductosEliminar());

        actual.setInventarioVer(p.getInventarioVer());
        actual.setInventarioCrear(p.getInventarioCrear());
        actual.setInventarioEditar(p.getInventarioEditar());

        actual.setClientesVer(p.getClientesVer());
        actual.setClientesCrear(p.getClientesCrear());
        actual.setClientesEditar(p.getClientesEditar());
        actual.setClientesEliminar(p.getClientesEliminar());

        actual.setReportesVer(p.getReportesVer());

        actual.setUsuariosVer(p.getUsuariosVer());
        actual.setUsuariosCrear(p.getUsuariosCrear());
        actual.setUsuariosEditar(p.getUsuariosEditar());
        actual.setUsuariosEliminar(p.getUsuariosEliminar());

        actual.setConfiguracionVer(p.getConfiguracionVer());
        actual.setConfiguracionEditar(p.getConfiguracionEditar());

        return permisoRolRepository.save(actual);
    }
}
