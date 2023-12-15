/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Modelo.ConsultasProducto;
import Modelo.Alumnos;
import Vista.frmEditarProducto;
import Vista.frmNuevoProducto;
import Vista.frmTablaProductos;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jose Refugio
 */
public  class CtrlProductos implements ActionListener, MouseListener, WindowListener {
    private Alumnos mod;
    private ConsultasProducto modC;
    private frmEditarProducto frmEditar;
    private frmTablaProductos frmTabla;
    private frmNuevoProducto frmNuevo;
    private String idProduct = "";
    private int[] idsProduct;
    private boolean asistencia = true;
    
    
    //the width of fingerprint image
	int fpWidth = 0;
	//the height of fingerprint image
	int fpHeight = 0;
	//for verify test
	private byte[] lastRegTemp = new byte[2048];
	//the length of lastRegTemp
	private int cbRegTemp = 0;
	//pre-register template
	private byte[][] regtemparray = new byte[3][2048];
	//Register
	private boolean bRegister = false;
	//Identify
	private boolean bIdentify = true;
	//finger id
	private int iFid = 1;
	
	private int nFakeFunOn = 1;
	//must be 3
	static final int enroll_cnt = 3;
	//the index of pre-register function
	private int enroll_idx = 0;
	
	private byte[] imgbuf = null;
	private byte[] template = new byte[2048];
	private int[] templateLen = new int[1];
	
	
	private boolean mbStop = true;
	private long mhDevice = 0;
	private long mhDB = 0;
	private WorkThread workThread = null;

    public CtrlProductos(Alumnos mod, ConsultasProducto modC, frmEditarProducto frmEditar, frmTablaProductos frmTabla, frmNuevoProducto frmNuevo){

            this.mod = mod;
            this.modC = modC;
            this.frmEditar = frmEditar;
            this.frmTabla = frmTabla;
            this.frmNuevo = frmNuevo;

            this.frmEditar.btnModificar.addActionListener(this);
            this.frmNuevo.btnGuardar.addActionListener(this);
            this.frmTabla.btnNewProduct.addActionListener(this);
            this.frmTabla.btnEditProduct.addActionListener(this);
            this.frmTabla.btnDeletedProduct.addActionListener(this);
            this.frmTabla.tblProductos.addMouseListener(this);
            this.frmTabla.addWindowListener(this);
            
    }

    public void iniciar(){
       
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("Codigo");
        model.addColumn("Nombre");
        model.addColumn("Apellidos");
        model.addColumn("Direccion");
        model.addColumn("No. control");
        idsProduct = modC.alumnos(model, idsProduct);
        
        if(idsProduct.length > 0 ){
             openSensor();
            cargarHuellas();

                frmTabla.tblProductos.setModel(model);
                frmTabla.setVisible(true);


        } else {
                JOptionPane.showMessageDialog(null, "No se encontro registro");	
                openSensor();
                frmTabla.setVisible(true);
        }
    }
        
    
        
    @Override
    public void actionPerformed(ActionEvent e)
    {


// ++ ELIMINAR ++
        if(e.getSource() == frmTabla.btnDeletedProduct){
            if(this.idProduct != ""){
                mod.setId( Integer.parseInt(this.idProduct));

                if(modC.eliminar(mod)){

                        JOptionPane.showMessageDialog(null, "Registro Eliminado");
                        renderizarProductos();


                } else {
                        JOptionPane.showMessageDialog(null, "Error al Eliminar");	

                }
            }else{

                JOptionPane.showMessageDialog(null, "Selecciona un producto");	
            }
        }


        if(e.getSource() == frmTabla.btnEditProduct){
            if(this.idProduct != ""){

                mod.setId( Integer.parseInt(this.idProduct));

                if(modC.buscar(mod)){
                    frmEditar.setVisible(true);

                        frmEditar.txtId.setText(String.valueOf(mod.getId()));
                        frmEditar.txtCodigo.setText(mod.getCodigo());
                        frmEditar.txtNombre.setText(mod.getNombre());
                        frmEditar.txtPrecio.setText(mod.getApellidos());
                        frmEditar.txtCantidad.setText(mod.getDireccion());
                        frmEditar.txtControl.setText(mod.getNocontrol());

                } else {
                        JOptionPane.showMessageDialog(null, "No se encontro registro");	

                }
            }else{

                JOptionPane.showMessageDialog(null, "Selecciona un producto");	
            }

        }
        
        if(e.getSource() == frmTabla.btnNewProduct){
            modC.ultimoID(mod);
            frmNuevo.setVisible(true);
            
            //openSensor();
            
            if(0 == mhDevice)
            {
                frmNuevo.textArea.setText("Abre primero el lector");
                return;
            }
            if(!bRegister)
            {
                enroll_idx = 0;
                bRegister = true;
                frmNuevo.textArea.setText("Coloca tres veces tu dedo");
            }
        }
        
         if(e.getSource() == frmNuevo.btnGuardar){
            mod.setCodigo(frmNuevo.txtCodigo.getText());
            mod.setNombre(frmNuevo.txtNombre.getText());
            mod.setApellidos(frmNuevo.txtPrecio.getText());
            mod.setDireccion(frmNuevo.txtCantidad.getText());
            mod.setNocontrol(frmNuevo.txtControl.getText());

            if(modC.registrar(mod)){

                    JOptionPane.showMessageDialog(null, "Registro Guardado");
                    limpiarNuevo();
                    renderizarProductos();
                    
                    frmNuevo.setVisible(false);
                    

            } else {
                    JOptionPane.showMessageDialog(null, "Error al Guardar");	
                    limpiarNuevo();
            }
        }
         
          if(e.getSource() == frmEditar.btnModificar){
            mod.setId( Integer.parseInt(frmEditar.txtId.getText()));
            mod.setCodigo(frmEditar.txtCodigo.getText());
            mod.setNombre(frmEditar.txtNombre.getText());
            mod.setApellidos(frmEditar.txtPrecio.getText());
            mod.setDireccion(frmEditar.txtCantidad.getText());
            mod.setNocontrol(frmEditar.txtControl.getText());

            if(modC.modificar(mod)){

                    JOptionPane.showMessageDialog(null, "Registro Modificado");
                    renderizarProductos();
                    frmEditar.setVisible(false);
                    

            } else {
                    JOptionPane.showMessageDialog(null, "Error al Modificar");	
                    
            }
        }



    }
    
    public void limpiarNuevo(){
        frmNuevo.txtCodigo.setText(null);
        frmNuevo.txtNombre.setText(null);
        frmNuevo.txtPrecio.setText(null);
        frmNuevo.txtCantidad.setText(null);
    }
     
    public void renderizarProductos(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("Codigo");
        model.addColumn("Nombre");
        model.addColumn("Apellidos");
        model.addColumn("Direccion");
        model.addColumn("No. control");
        idsProduct = modC.alumnos(model, idsProduct);

        if(idsProduct.length > 0 ){


                frmTabla.tblProductos.setModel(model);

        } else {
                JOptionPane.showMessageDialog(null, "No se encontro registro");	

        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == frmTabla.tblProductos){
            
            this.idProduct = frmTabla.tblProductos.getValueAt(frmTabla.tblProductos.getSelectedRow(), 0).toString() ;
            }
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
        if(e.getSource() == frmTabla){
            FreeSensor();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
       
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    private void FreeSensor()
	{
		mbStop = true;
		try {		//wait for thread stopping
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (0 != mhDB)
		{
			FingerprintSensorEx.DBFree(mhDB);
			mhDB = 0;
		}
		if (0 != mhDevice)
		{
			FingerprintSensorEx.CloseDevice(mhDevice);
			mhDevice = 0;
		}
		FingerprintSensorEx.Terminate();
	}
	
	public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
			String path) throws IOException {
		java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
		java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

		int w = (((nWidth+3)/4)*4);
		int bfType = 0x424d;
		int bfSize = 54 + 1024 + w * nHeight;
		int bfReserved1 = 0;
		int bfReserved2 = 0;
		int bfOffBits = 54 + 1024;

		dos.writeShort(bfType); 
		dos.write(changeByte(bfSize), 0, 4); 
		dos.write(changeByte(bfReserved1), 0, 2);
		dos.write(changeByte(bfReserved2), 0, 2);
		dos.write(changeByte(bfOffBits), 0, 4);

		int biSize = 40;
		int biWidth = nWidth;
		int biHeight = nHeight;
		int biPlanes = 1;
		int biBitcount = 8;
		int biCompression = 0;
		int biSizeImage = w * nHeight;
		int biXPelsPerMeter = 0;
		int biYPelsPerMeter = 0;
		int biClrUsed = 0;
		int biClrImportant = 0;

		dos.write(changeByte(biSize), 0, 4);
		dos.write(changeByte(biWidth), 0, 4);
		dos.write(changeByte(biHeight), 0, 4);
		dos.write(changeByte(biPlanes), 0, 2);
		dos.write(changeByte(biBitcount), 0, 2);
		dos.write(changeByte(biCompression), 0, 4);
		dos.write(changeByte(biSizeImage), 0, 4);
		dos.write(changeByte(biXPelsPerMeter), 0, 4);
		dos.write(changeByte(biYPelsPerMeter), 0, 4);
		dos.write(changeByte(biClrUsed), 0, 4);
		dos.write(changeByte(biClrImportant), 0, 4);

		for (int i = 0; i < 256; i++) {
			dos.writeByte(i);
			dos.writeByte(i);
			dos.writeByte(i);
			dos.writeByte(0);
		}

		byte[] filter = null;
		if (w > nWidth)
		{
			filter = new byte[w-nWidth];
		}
		
		for(int i=0;i<nHeight;i++)
		{
			dos.write(imageBuf, (nHeight-1-i)*nWidth, nWidth);
			if (w > nWidth)
				dos.write(filter, 0, w-nWidth);
		}
		dos.flush();
		dos.close();
		fos.close();
	}

	public static byte[] changeByte(int data) {
		return intToByteArray(data);
	}
	
	public static byte[] intToByteArray (final int number) {
		byte[] abyte = new byte[4];  
	     
	    abyte[0] = (byte) (0xff & number);  
	     
	    abyte[1] = (byte) ((0xff00 & number) >> 8);  
	    abyte[2] = (byte) ((0xff0000 & number) >> 16);  
	    abyte[3] = (byte) ((0xff000000 & number) >> 24);  
	    return abyte; 
	}	 
		 
		public static int byteArrayToInt(byte[] bytes) {
			int number = bytes[0] & 0xFF;  
		  
		    number |= ((bytes[1] << 8) & 0xFF00);  
		    number |= ((bytes[2] << 16) & 0xFF0000);  
		    number |= ((bytes[3] << 24) & 0xFF000000);  
		    return number;  
		 }

    @Override
    public void windowOpened(WindowEvent e) {
       
    }

    @Override
    public void windowClosed(WindowEvent e) {
       
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
      
    }

    @Override
    public void windowActivated(WindowEvent e) {
       
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }

   
	
		private class WorkThread extends Thread {
	        @Override
	        public void run() {
	            super.run();
	            int ret = 0;
	            while (!mbStop) {
	            	templateLen[0] = 2048;
	            	if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen)))
	            	{
	            		if (nFakeFunOn == 1)
                    	{
                    		byte[] paramValue = new byte[4];
            				int[] size = new int[1];
            				size[0] = 4;
            				int nFakeStatus = 0;
            				//GetFakeStatus
            				ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
            				nFakeStatus = byteArrayToInt(paramValue);
            				System.out.println("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
            				if (0 == ret && (byte)(nFakeStatus & 31) != 31)
            				{
            					frmNuevo.textArea.setText("Is a fake-finer?");
            					return;
            				}
                    	}
                    	OnCatpureOK(imgbuf);
                    	OnExtractOK(template, templateLen[0]);
	            	}
	                try {
	                    Thread.sleep(500);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }

	            }
	        }

			private void runOnUiThread(Runnable runnable) {
				// TODO Auto-generated method stub
				
			}
	    }
		
		private void OnCatpureOK(byte[] imgBuf)
		{
			try {
                            if(frmNuevo.isVisible()){
                            writeBitmap(imgBuf, fpWidth, fpHeight, "src/huellas/"+(mod.getId()+1)+".bmp");
				frmNuevo.btnImg.setIcon(new ImageIcon(ImageIO.read(new File("src/huellas/"+(mod.getId()+1)+".bmp"))));
                                frmNuevo.txtCodigo.setText("src/huellas/"+(mod.getId()+1)+".bmp");
                            }else{
                                System.out.println(fpWidth + "  "+fpHeight);
                                
                                writeBitmap(imgBuf, fpWidth, fpHeight, "src/huellas/fingerprintComp.bmp");
				frmTabla.btnImg.setIcon(new ImageIcon(((new ImageIcon(ImageIO.read(new File("src/huellas/fingerprintComp.bmp")))).getImage()).getScaledInstance(frmTabla.btnImg.getWidth(),frmTabla.btnImg.getHeight() , java.awt.Image.SCALE_SMOOTH)));
                               
                            
                            
                            }
                                
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void OnExtractOK(byte[] template, int len)
		{
			if(bRegister)
			{
                            int[] fid = new int[1];
                            int[] score = new int [1];
                            int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                            if (ret == 0)
                            {
                                frmNuevo.textArea.setText("the finger already enroll by " + fid[0] + ",cancel enroll");
                                bRegister = false;
                                enroll_idx = 0;
                                return;
                            }
                            if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx-1], template) <= 0)
                            {
                                    frmNuevo.textArea.setText("please press the same finger 3 times for the enrollment");
                                return;
                            }
                            System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
                            enroll_idx++;
                            if (enroll_idx == 3) {
                                    int[] _retLen = new int[1];
                                _retLen[0] = 2048;
                                byte[] regTemp = new byte[_retLen[0]];

                                if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen)) &&
                                            0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                                    iFid++;
                                    cbRegTemp = _retLen[0];
                                    System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                                    //Base64 Template
                                    frmNuevo.textArea.setText("Huella registrada correctamente");
                                } else {
                                    frmNuevo.textArea.setText("fallo registro de huella, error codigo=" + ret);
                                }
                                bRegister = false;
                            } else {
                                    frmNuevo.textArea.setText("tienes que ingresar " + (3 - enroll_idx) + " veces la huella");
                            }
			}
			else
			{
                            if (bIdentify)
                            {
                                    int[] fid = new int[1];
                                    int[] score = new int [1];
                                    int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                                    if (ret == 0)
                                    {
                                        frmNuevo.textArea.setText("Identificacion exitosa, Huellaid=" + fid[0] + ",coincidencia=" + score[0]);
                                    }
                                    else
                                    {
                                        frmNuevo.textArea.setText("identificacion fallida, errcode=" + ret);
                                    }

                            }
                            else
                            {
                                if(asistencia){
                                    int[] fid = new int[1];
                                    int[] score = new int [1];
                                    int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                                    if (ret == 0)
                                    {
                                        if(modC.registrarAsistencia( fid[0])){
                                            
                                            mod.setId(fid[0]);

                                            if(modC.buscar(mod)){

                                                   frmTabla.textArea.setText("Asistencia tomada del usuario: " + mod.getNombre( ));
                                                    

                                            } else {
                                                    JOptionPane.showMessageDialog(null, "No se encontro registro");	

                                            }
                                            
                                            
                                        }
                                        
                                        
                                    }
                                    else
                                    {
                                        frmTabla.textArea.setText("Usuario no identificado");
                                    }
                                    
                                }else{
                                    if(cbRegTemp <= 0)
                                    {
                                            frmNuevo.textArea.setText("realizar registro primero");
                                    }
                                    else
                                    {
                                            int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
                                            if(ret > 0)
                                            {
                                                    frmNuevo.textArea.setText("verificacion exitosa , score=" + ret);
                                            }
                                            else
                                            {
                                                    frmNuevo.textArea.setText("verificacion fallida, ret=" + ret);
                                            }
                                    }
                                }
                                    
                            }
			}
		}
                
                
                private void openSensor(){
                if (0 != mhDevice)
				{
					//already inited
					frmNuevo.textArea.setText("Please close device first!");
					return;
				}
				int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
				//Initialize
				cbRegTemp = 0;
				bRegister = false;
				bIdentify = false;
				iFid = 1;
				enroll_idx = 0;
				if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init())
				{
					frmNuevo.textArea.setText("Init failed!");
					return;
				}
				ret = FingerprintSensorEx.GetDeviceCount();
				if (ret < 0)
				{
					frmTabla.textArea.setText("dispositivo no conectado");
					FreeSensor();
					return;
				}
				if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0)))
				{
					frmNuevo.textArea.setText("dispositivo no detectado, ret = " + ret + "!");
					FreeSensor();
					return;
				}
				if (0 == (mhDB = FingerprintSensorEx.DBInit()))
				{
					frmNuevo.textArea.setText("Init DB fail, ret = " + ret + "!");
					FreeSensor();
					return;
				}
				
				//For ISO/Ansi
				int nFmt = 0;	//Ansi
				
				FingerprintSensorEx.DBSetParameter(mhDB,  5010, nFmt);				
				//For ISO/Ansi End
				
				//set fakefun off
				//FingerprintSensorEx.SetParameter(mhDevice, 2002, changeByte(nFakeFunOn), 4);
				
				byte[] paramValue = new byte[4];
				int[] size = new int[1];
				//GetFakeOn
				//size[0] = 4;
				//FingerprintSensorEx.GetParameters(mhDevice, 2002, paramValue, size);
				//nFakeFunOn = byteArrayToInt(paramValue);
				
				size[0] = 4;
				FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
				fpWidth = byteArrayToInt(paramValue);
				size[0] = 4;
				FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
				fpHeight = byteArrayToInt(paramValue);
				//width = fingerprintSensor.getImageWidth();
				//height = fingerprintSensor.getImageHeight();
				imgbuf = new byte[fpWidth*fpHeight];
				frmNuevo.btnImg.resize(fpWidth, fpHeight);
                                frmTabla.btnImg.resize(fpWidth, fpHeight);
				mbStop = false;
				workThread = new WorkThread();
			    workThread.start();
	            frmNuevo.textArea.setText("Open succ!");
                
                }
                
                private void cargarHuellas(){
                    
                    if(0 == mhDB)
                    {
                            frmTabla.textArea.setText("Please open device first!");
                    }

                    for(int i = 0; i< idsProduct.length; i++){
                        String path = "src/huellas/"+idsProduct[i]+".bmp";
                        
                        byte[] fpTemplate = new byte[2048];
                        int[] sizeFPTemp = new int[1];
                        sizeFPTemp[0] = 2048;
                        int ret = FingerprintSensorEx.ExtractFromImage( mhDB, path, 500, fpTemplate, sizeFPTemp);
                        if (0 == ret)
                        {
                                ret = FingerprintSensorEx.DBAdd( mhDB, idsProduct[i], fpTemplate);
                                if (0 == ret)
                                {
                                        //String base64 = fingerprintSensor.BlobToBase64(fpTemplate, sizeFPTemp[0]);		
                                       // iFid++;
                                        cbRegTemp = sizeFPTemp[0];
                                        System.arraycopy(fpTemplate, 0, lastRegTemp, 0, cbRegTemp);
                                        //Base64 Template
                                        //String strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                        frmTabla.textArea.setText("huella cargada correctamente");
                                        System.out.println("huella cargada correctamente");
                                }
                                else
                                {
                                        frmTabla.textArea.setText("DBAdd fail, ret=" + ret);
                                }
                        }
                        else
                        {
                                frmTabla.textArea.setText("ExtractFromImage fail, ret=" + ret);
                        }

                    }
                    
				
                
                }
    
    
    


    
}


