package mvcbasicoproducto;

import Controlador.CtrlProducto;
import Controlador.CtrlProductos;
import Modelo.ConsultasProducto;
import Modelo.Alumnos;
import Vista.*;

/**
 *
 * @author IngYuliver
 */
public class MvcBasicoProducto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Alumnos mod = new Alumnos();
        ConsultasProducto modC = new ConsultasProducto();
        frmEditarProducto frmEditar = new frmEditarProducto();
        frmTablaProductos frmTabla = new frmTablaProductos();
        frmNuevoProducto frmNuevo = new frmNuevoProducto();

        CtrlProductos ctrlProductos = new CtrlProductos(mod,modC,frmEditar, frmTabla,frmNuevo);

        ctrlProductos.iniciar();

    }
    
}
