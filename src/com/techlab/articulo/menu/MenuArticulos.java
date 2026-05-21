package com.techlab.articulo.menu;

import java.util.Scanner;
import com.techlab.articulo.model.Articulo;
import com.techlab.articulo.model.ArticuloAlimenticio;
import com.techlab.articulo.model.ArticuloElectronico;
import com.techlab.articulo.model.Categoria;
import com.techlab.articulo.repository.Repositorio;
import com.techlab.articulo.utils.Secuencias;
import com.techlab.articulo.utils.Validaciones;
/**
 * CONSIGNA DE ESTA CLASE
 * ------------------------------------------------------------
 * Esta clase debe heredar de Menu y encargarse del CRUD de artículos.
 *
 * Debe trabajar con:
 * - Repositorio<Articulo>
 * - Repositorio<Categoria>
 *
 * ¿Por qué necesita también categorías?
 * Porque un artículo debe asociarse a una categoría ya existente.
 *
 * FUNCIONALIDADES ESPERADAS
 * ------------------------------------------------------------
 * 1) Ingresar artículo
 * 2) Listar artículos
 * 3) Consultar un artículo por código
 * 4) Modificar un artículo
 * 5) Eliminar un artículo
 * 0) Volver
 *
 * REQUISITOS IMPORTANTES
 * ------------------------------------------------------------
 * - Antes de crear un artículo, debe verificarse que existan categorías.
 * - Debe preguntarse qué tipo de artículo se quiere crear:
 *   - electrónico
 *   - alimenticio
 * - Debe pedirse:
 *   - nombre
 *   - precio
 *   - categoría por código
 * - Si es electrónico:
 *   - garantía en meses
 * - Si es alimenticio:
 *   - días para vencimiento
 *
 * VALIDACIONES
 * ------------------------------------------------------------
 * - nombre no vacío
 * - precio no negativo
 * - categoría existente
 * - garantía no negativa
 * - días para vencimiento no negativos
 *
 * SUGERENCIA DE MÉTODOS
 * ------------------------------------------------------------
 * - ingresarArticulo()
 * - listarArticulos()
 * - consultarArticulo()
 * - modificarArticulo()
 * - eliminarArticulo()
 * - pedirCategoriaExistente()
 * - pedirNombreArticulo()
 * - pedirPrecioArticulo()
 * - pedirGarantia()
 * - pedirDiasParaVencimiento()
 */
public class MenuArticulos extends Menu {

    private Repositorio<Articulo> repoArticulos;
    private Repositorio<Categoria> repoCategorias;

    public MenuArticulos(java.util.Scanner scanner, Repositorio<Articulo> repoArticulos, Repositorio<Categoria> repoCategorias) {
        super(scanner);
        this.repoArticulos = repoArticulos;
        this.repoCategorias = repoCategorias;
    }

    @Override
    public void mostrarMenu() {
        System.out.println("\n--- MENÚ ARTÍCULOS ---");
        System.out.println("1 - Ingresar artículo");
        System.out.println("2 - Listar artículos");
        System.out.println("3 - Consultar artículo");
        System.out.println("4 - Modificar artículo");
        System.out.println("5 - Eliminar artículo");
        System.out.println("0 - Volver");
    }

    @Override
    public void ejecutar() {
        // TODO:
        // Implementar el loop del menú y llamar a los métodos correspondientes.
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Ingrese una opción: ");

            switch (opcion) {
                case 1: ingresarArticulo(); break;
                case 2: listarArticulos(); break;
                case 3: consultarArticulo(); break;
                case 4: modificarArticulo(); break;
                case 5: eliminarArticulo(); break;
                case 0: System.out.println("Volviendo al menú principal..."); break;
                default: System.out.println("Error: opción no válida.");
            }
        } 
        while (opcion != 0);
    }

    // TODO:
    // Implementar todos los métodos del CRUD de artículos.

    private void ingresarArticulo() {
        System.out.println("\n--- INGRESAR ARTÍCULO ---");
        
        if (repoCategorias.estaVacio()) {
            System.out.println("Error: No hay categorías registradas. Por favor, cree una categoría primero.");
            return;
        }

        System.out.println("¿Qué tipo de artículo desea ingresar?");
        System.out.println("1 - Electrónico");
        System.out.println("2 - Alimenticio");
        
        int tipo = leerEntero("Seleccione el tipo (1 o 2): ");
        if (tipo != 1 && tipo != 2) {
            System.out.println("Error: Tipo de artículo no válido.");
            return;
        }

        String nombre = leerTexto("Ingrese el nombre del artículo: ");
        
        double precio = -1;
        while (precio < 0) {
            precio = leerDouble("Ingrese el precio base: ");
            if (precio < 0) System.out.println("Error: El precio no puede ser negativo.");
        }

        Categoria categoria = pedirCategoriaExistente();
        int codigo = Secuencias.generarCodigoArticulo();
        
        Articulo nuevoArticulo = null;

        if (tipo == 1) { 
            int garantia = -1;
            while (garantia < 0) {
                garantia = leerEntero("Ingrese los meses de garantía: ");
                if (garantia < 0) System.out.println("Error: La garantía no puede ser negativa.");
            }
            nuevoArticulo = new ArticuloElectronico(codigo, nombre, precio, categoria, garantia);
            
        } else { 
            int diasVencimiento = -1;
            while (diasVencimiento < 0) {
                diasVencimiento = leerEntero("Ingrese los días para el vencimiento: ");
                if (diasVencimiento < 0) System.out.println("Error: Los días no pueden ser negativos.");
            }
            nuevoArticulo = new ArticuloAlimenticio(codigo, nombre, precio, categoria, diasVencimiento);
        }

        repoArticulos.agregar(nuevoArticulo);
        System.out.println("Artículo ingresado exitosamente con el código: " + codigo);
    }

    private void listarArticulos() {
        System.out.println("\n--- LISTADO DE ARTÍCULOS ---");
        if (repoArticulos.estaVacio()) {
            System.out.println("No hay artículos registrados.");
            return;
        }
        
        for (Articulo a : repoArticulos.listar()) {
            System.out.println(a.toString());
        }
    }

    private void consultarArticulo() {
        System.out.println("\n--- CONSULTAR ARTÍCULO ---");
        if (repoArticulos.estaVacio()) {
            System.out.println("No hay artículos registrados.");
            return;
        }
        
        int codigo = leerEntero("Ingrese el código del artículo a buscar: ");
        Articulo a = repoArticulos.buscarPorCodigo(codigo);
        
        if (a == null) {
            System.out.println("Artículo no encontrado.");
        } else {
            System.out.println("Artículo encontrado:");
            System.out.println(a);
        }
    }

    private void modificarArticulo() {
        System.out.println("\n--- MODIFICAR ARTÍCULO ---");
        if (repoArticulos.estaVacio()) {
            System.out.println("No hay artículos registrados.");
            return;
        }
        
        int codigo = leerEntero("Ingrese el código del artículo a modificar: ");
        Articulo articulo = repoArticulos.buscarPorCodigo(codigo);
        
        if (articulo == null) {
            System.out.println("Artículo no encontrado.");
            return;
        }

        String nuevoNombre = leerTexto("Ingrese el nuevo nombre: ");
        double nuevoPrecio = -1;
        while (nuevoPrecio < 0) {
            nuevoPrecio = leerDouble("Ingrese el nuevo precio base: ");
            if (nuevoPrecio < 0) System.out.println("Error: El precio no puede ser negativo.");
        }
        
        System.out.println("¿Desea cambiar la categoría? (S/N)");
        if (leerSiNo("Su elección: ")) {
            Categoria nuevaCategoria = pedirCategoriaExistente();
            articulo.setCategoria(nuevaCategoria);
        }

        articulo.setNombre(nuevoNombre);
        articulo.setPrecio(nuevoPrecio);

        if (articulo instanceof ArticuloElectronico) {
            ArticuloElectronico electronico = (ArticuloElectronico) articulo;
            
            int nuevaGarantia = -1;
            while (nuevaGarantia < 0) {
                nuevaGarantia = leerEntero("Ingrese los nuevos meses de garantía: ");
                if (nuevaGarantia < 0) System.out.println("Error: No puede ser negativo.");
            }
            electronico.setGarantiaMeses(nuevaGarantia);
            
        } else if (articulo instanceof ArticuloAlimenticio) {
            ArticuloAlimenticio alimenticio = (ArticuloAlimenticio) articulo;
            
            int nuevosDias = -1;
            while (nuevosDias < 0) {
                nuevosDias = leerEntero("Ingrese los nuevos días para vencimiento: ");
                if (nuevosDias < 0) System.out.println("Error: No puede ser negativo.");
            }
            alimenticio.setDiasParaVencimiento(nuevosDias);
        }

        System.out.println("Artículo modificado exitosamente.");
    }

    private void eliminarArticulo() {
        System.out.println("\n--- ELIMINAR ARTÍCULO ---");
        if (repoArticulos.estaVacio()) {
            System.out.println("No hay artículos registrados.");
            return;
        }
        
        int codigo = leerEntero("Ingrese el código del artículo a eliminar: ");
        Articulo a = repoArticulos.buscarPorCodigo(codigo);
        
        if (a == null) {
            System.out.println("Artículo no encontrado.");
            return;
        }

        if (leerSiNo("¿Está seguro que desea eliminar '" + a.getNombre() + "'? (S/N): ")) {
            repoArticulos.eliminar(a);
            System.out.println("Artículo eliminado exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private Categoria pedirCategoriaExistente() {
        System.out.println("\nCategorías disponibles:");
        for (Categoria c : repoCategorias.listar()) {
            System.out.println("- Código " + c.getCodigo() + ": " + c.getNombre());
        }

        while (true) {
            int codigoCat = leerEntero("Ingrese el código de la categoría elegida: ");
            Categoria cat = repoCategorias.buscarPorCodigo(codigoCat);
            if (cat != null) {
                return cat;
            }
            System.out.println("Error: El código de categoría ingresado no existe. Intente de nuevo.");
        }
    }
}
