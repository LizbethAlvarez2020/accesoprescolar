package Controlador;

import Modelo.ConsultasProducto;
import Modelo.Alumnos;
import Vista.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class CtrlProducto implements ActionListener {

	private Alumnos mod;
	private ConsultasProducto modC;
	private frmProducto frm;
        private frmTablaProductos frmTabla;
        private int[] idsProduct;

	public CtrlProducto(Alumnos mod, ConsultasProducto modC, frmProducto frm, frmTablaProductos frmTabla){

		this.mod = mod;
		this.modC = modC;
		this.frm = frm;
                this.frmTabla = frmTabla;
               
		this.frm.btnGuardar.addActionListener(this);
		this.frm.btnModificar.addActionListener(this);
		this.frm.btnEliminar.addActionListener(this);
		this.frm.btnLimpiar.addActionListener(this);
		this.frm.btnBuscar.addActionListener(this);
                this.frm.btnProductos.addActionListener(this);
	}

	// iniciar vista
	public void iniciar(){
		frm.setTitle("Producto"); // establecer titulo del formulario
		frm.setLocationRelativeTo(null); // posicion del formulario centrado
		frm.txtId.setVisible(false); // caja de texto no visible txtId
	}

        @Override
	public void actionPerformed(ActionEvent e)
        {
		// detectar que boton preciona +++ GUARDAR ++
		if(e.getSource() == frm.btnGuardar){

			mod.setCodigo(frm.txtCodigo.getText());
			mod.setNombre(frm.txtNombre.getText());
			mod.setApellidos(frm.txtPrecio.getText());
                        mod.setDireccion(frm.txtCantidad.getText());
                        mod.setNocontrol(frm.txtControl.getText());

			if(modC.registrar(mod)){

				JOptionPane.showMessageDialog(null, "Registro Guardado");
				limpiar();

			} else {
				JOptionPane.showMessageDialog(null, "Error al Guardar");	
				limpiar();
			}
		}
// ++ MODIFICAR ++ 
		if(e.getSource() == frm.btnModificar){

			mod.setId( Integer.parseInt(frm.txtId.getText()));
			mod.setCodigo(frm.txtCodigo.getText());
			mod.setNombre(frm.txtNombre.getText());
			mod.setApellidos(frm.txtPrecio.getText());
                        mod.setDireccion(frm.txtCantidad.getText());
                        mod.setNocontrol(frm.txtControl.getText());

			if(modC.modificar(mod)){

				JOptionPane.showMessageDialog(null, "Registro Modificado");
				limpiar();

			} else {
				JOptionPane.showMessageDialog(null, "Error al Modificar");	
				limpiar();
			}
		}
// ++ ELIMINAR ++
		if(e.getSource() == frm.btnEliminar){

			mod.setId( Integer.parseInt(frm.txtId.getText()));

			if(modC.eliminar(mod)){

				JOptionPane.showMessageDialog(null, "Registro Eliminado");
				limpiar();

			} else {
				JOptionPane.showMessageDialog(null, "Error al Eliminar");	
				limpiar();
			}
		}
// ++ BUSCAR ++
		if(e.getSource() == frm.btnBuscar){

			mod.setCodigo(frm.txtCodigo.getText());

			if(modC.buscar(mod)){

				frm.txtId.setText(String.valueOf(mod.getId()));
				frm.txtCodigo.setText(mod.getCodigo());
				frm.txtNombre.setText(mod.getNombre());
				frm.txtPrecio.setText(mod.getApellidos());
                                frm.txtCantidad.setText(mod.getDireccion());
                                frm.txtControl.setText(mod.getNocontrol());

			} else {
				JOptionPane.showMessageDialog(null, "No se encontro registro");	
				limpiar();
			}
		}

		if(e.getSource() == frm.btnLimpiar){
			limpiar();
		}
                
                if(e.getSource() == frm.btnProductos){
                    
                    DefaultTableModel model = new DefaultTableModel();
                    model.addColumn("id");
                    model.addColumn("Codigo");
                    model.addColumn("Nombre");
                    model.addColumn("Precio");
                    model.addColumn("Cantidad");
                    
                    
                    
                    
                    
                   
			if(modC.alumnos(model, idsProduct).length > 0 ){
                            
                                System.out.println(idsProduct);
				frmTabla.tblProductos.setModel(model);
                                frmTabla.setVisible(true);
                                

			} else {
				JOptionPane.showMessageDialog(null, "No se encontro registro");	
				limpiar();
			}
                    
                    
		}
                
                

	}

	public void limpiar(){
		frm.txtId.setText(null);
		frm.txtCodigo.setText(null);
		frm.txtNombre.setText(null);
		frm.txtPrecio.setText(null);
		frm.txtCantidad.setText(null);
        }
}