
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class ConsultasProducto extends Conexion{
    
 /*
 * CRUD producto
 * Resive: modelo Producto
 * Retorna: Boolean
 */
    public boolean registrar(Alumnos pro){
        
        PreparedStatement ps = null;
        Connection con = getConexion();
        
        String sql = "INSERT INTO alumnos (codigo,nombre,apellidos,direccion,nocontrol) VALUES (?,?,?,?,?)";
        
        try{
            
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getApellidos());
            ps.setString(4, pro.getDireccion());
            ps.setString(5, pro.getNocontrol());
            ps.execute();
            
            return true;
            
        }catch(SQLException e){
            System.err.println(e);
            return false;
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion registrar");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }  

    public boolean modificar(Alumnos pro){
        
        PreparedStatement ps = null;
        Connection con = getConexion();
        
        String sql = "UPDATE alumnos SET codigo=?, nombre=?, apellidos=? ,direccion=?, nocontrol=? WHERE id=?";
        
        try{
            
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getApellidos());
            ps.setString(4, pro.getDireccion());
            ps.setString(5, pro.getNocontrol());
            ps.setInt(6, pro.getId());
            ps.execute();
            
            return true;
            
        }catch(SQLException e){
            System.err.println(e);
            return false;
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion modificar");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }  

    public boolean eliminar(Alumnos pro){
        
        PreparedStatement ps = null;
        Connection con = getConexion();
        
        String sql = "DELETE FROM alumnos WHERE id=?";
        
        try{
            
            ps = con.prepareStatement(sql);  
            ps.setInt(1, pro.getId());
            ps.execute();
            
            return true;
            
        }catch(SQLException e){
            System.err.println(e);
            return false;
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion eliminar");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }  

    public boolean buscar(Alumnos pro){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = getConexion();
        
        String sql = "SELECT * FROM alumnos WHERE id=?";
        
        try{
            
            ps = con.prepareStatement(sql);  
            ps.setInt(1, pro.getId());
            rs = ps.executeQuery();
            
            if(rs.next()){

                pro.setId( Integer.parseInt(rs.getString("id")));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setApellidos(rs.getString("apellidos"));
                pro.setDireccion(rs.getString("direccion"));
                pro.setNocontrol(rs.getString("nocontrol"));

                return true;
            }

            return false;
            
        }catch(SQLException e){
            System.err.println(e);
            return false;
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion buscar");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }  
    
    public int[] alumnos(DefaultTableModel model, int[] idsProduct){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = getConexion();
        
        String sql = "SELECT alumnos.*, count(*) over () total_rows FROM alumnos";
        
        String[] dato =new String[6];
        int[] ids;
        
        try{
            
            ps = con.prepareStatement(sql);  
            rs = ps.executeQuery();
            
            int rowsNum = 0;
            int contador = 0;
            if(rs.next()){
            rowsNum =rs.getInt("total_rows");
            ids = new int[rowsNum];
               do{
                   ids[contador] = Integer.parseInt(rs.getString(1));
                   
                   dato[0] = rs.getString(1);
                   dato[1] = rs.getString(2);
                   dato[2] = rs.getString(3);
                   dato[3] = rs.getString(4);
                   dato[4] = rs.getString(5);
                   dato[5] = rs.getString(6);
              
                   model.addRow(dato);
                   contador++;
               
                }while(rs.next());
               

                return ids;
            }else{
                    return new int[0];
                }
            
            
        }catch(SQLException e){
            System.err.println(e);
            return new int[0];
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion alumnos");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }
    
    public boolean ultimoID(Alumnos pro){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = getConexion();
        
        String sql = "SELECT max(id) FROM alumnos";
        
        try{
            
            ps = con.prepareStatement(sql);  
            rs = ps.executeQuery();
            rs.next();
            
            if(rs.getString(1) != null){
                System.out.println(rs.getString(1));
                pro.setId( Integer.parseInt(rs.getString(1)));
                
                return true;
            }
            pro.setId(0);
            return false;
            
        }catch(SQLException e){
            System.err.println(e);
            return false;
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion buscar");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }
    
    public boolean registrarAsistencia(int idUsuario){
        
        PreparedStatement ps = null;
        Connection con = getConexion();
       
        
        String sql = "INSERT INTO asistencia (idUsuario,asistencia,fecha) VALUES (?,?,now())";
        
        try{
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setBoolean(2, true);
            ps.execute();
            
            return true;
            
        }catch(SQLException e){
            System.err.println(e);
            return false;
        }finally{
            try{
                con.close();
                System.out.println("Cerrar conexion registrar asistencia");
            }catch(SQLException e){
                System.err.println(e);
            }
        }
    }  
}
