package com.techlab.articulo.menu;

import java.util.Scanner;
import com.techlab.articulo.model.Articulo;
import com.techlab.articulo.model.Categoria;
import com.techlab.articulo.repository.Repositorio;
import com.techlab.articulo.utils.Secuencias;

/**
 * CONSIGNA DE ESTA CLASE
 * ------------------------------------------------------------
 * Esta clase debe heredar de Menu y encargarse del CRUD de categorías.
 *
 * Debe trabajar con:
 * - Repositorio<Categoria>
 * - Repositorio<Articulo>
 *
 * ¿Por qué necesita también artículos?
 * Porque antes de eliminar una categoría debe verificarse si está
 * siendo utilizada por algún artículo.
 *
 * FUNCIONALIDADES ESPERADAS
 * ------------------------------------------------------------
 * 1) Ingresar categoría
 * 2) Listar categorías
 * 3) Consultar una categoría por código
 * 4) Modificar una categoría
 * 5) Eliminar una categoría
 * 0) Volver
 *
 * VALIDACIONES
 * ------------------------------------------------------------
 * - nombre no vacío
 * - descripción no vacía
 * - no permitir categorías repetidas por nombre
 *
 * REGLA DE NEGOCIO IMPORTANTE
 * ------------------------------------------------------------
 * No se puede eliminar una categoría si existe al menos un artículo
 * asociado a ella.
 *
 * SUGERENCIA DE MÉTODOS
 * ------------------------------------------------------------
 * - ingresarCategoria()
 * - listarCategorias()
 * - consultarCategoria()
 * - modificarCategoria()
 * - eliminarCategoria()
 * - categoriaTieneArticulosAsociados(...)
 */
public class MenuCategorias extends Menu {

    private Repositorio<Categoria> repoCategorias;
    private Repositorio<Articulo> repoArticulos;

    public MenuCategorias(java.util.Scanner scanner, Repositorio<Categoria> repoCategorias, Repositorio<Articulo> repoArticulos) {
        super(scanner);
        this.repoCategorias = repoCategorias;
        this.repoArticulos = repoArticulos;
    }

    @Override
    public void mostrarMenu() {
        System.out.println("\n--- MENÚ CATEGORÍAS ---");
        System.out.println("1 - Ingresar categoría");
        System.out.println("2 - Listar categorías");
        System.out.println("3 - Consultar categoría");
        System.out.println("4 - Modificar categoría");
        System.out.println("5 - Eliminar categoría");
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
                case 1: ingresarCategoria(); break;
                case 2: listarCategorias(); break;
                case 3: consultarCategoria(); break;
                case 4: modificarCategoria(); break;
                case 5: eliminarCategoria(); break;
                case 0: System.out.println("Volviendo al menú principal..."); break;
                default: System.out.println("Error: opción no válida.");
            }
        } while (opcion != 0);
    }

    // TODO:
    // Implementar todos los métodos del CRUD de categorías.

    private void ingresarCategoria() {
        System.out.println("\n--- INGRESAR CATEGORÍA ---");
        String nombre = leerTexto("Ingrese el nombre de la categoría: ");

        if (existeCategoriaPorNombre(nombre)) {
            System.out.println("Error: Ya existe una categoría con ese nombre.");
            return;
        }

        String descripcion = leerTexto("Ingrese la descripción: ");
        
        int codigo = Secuencias.generarCodigoCategoria();
        
        Categoria nueva = new Categoria(codigo, nombre, descripcion);
        repoCategorias.agregar(nueva);
        System.out.println("Categoría ingresada exitosamente con el código: " + codigo);
    }

    private void listarCategorias() {
        System.out.println("\n--- LISTADO DE CATEGORÍAS ---");
        if (repoCategorias.estaVacio()) {
            System.out.println("No hay categorías registradas.");
            return;
        }
        for (Categoria c : repoCategorias.listar()) {
            System.out.println(c);
        }
    }

    private void consultarCategoria() {
        System.out.println("\n--- CONSULTAR CATEGORÍA ---");
        if (repoCategorias.estaVacio()) {
            System.out.println("No hay categorías registradas.");
            return;
        }
        int codigo = leerEntero("Ingrese el código de la categoría a buscar: ");
        Categoria c = repoCategorias.buscarPorCodigo(codigo);
        
        if (c == null) {
            System.out.println("Categoría no encontrada.");
        } 
        else {
            System.out.println("Categoría encontrada: " + c);
        }
    }

    private void modificarCategoria() {
        System.out.println("\n--- MODIFICAR CATEGORÍA ---");
        if (repoCategorias.estaVacio()) {
            System.out.println("No hay categorías registradas.");
            return;
        }
        int codigo = leerEntero("Ingrese el código de la categoría a modificar: ");
        Categoria cat = repoCategorias.buscarPorCodigo(codigo);
        
        if (cat == null) {
            System.out.println("Categoría no encontrada.");
            return;
        }

        String nuevoNombre = leerTexto("Ingrese el nuevo nombre: ");
        if (!cat.getNombre().equalsIgnoreCase(nuevoNombre) && existeCategoriaPorNombre(nuevoNombre)) {
            System.out.println("Error: Ya existe otra categoría con ese nombre.");
            return;
        }

        String nuevaDesc = leerTexto("Ingrese la nueva descripción: ");
        
        cat.setNombre(nuevoNombre);
        cat.setDescripcion(nuevaDesc);
        System.out.println("Categoría modificada exitosamente.");
    }

    private void eliminarCategoria() {
        System.out.println("\n--- ELIMINAR CATEGORÍA ---");
        if (repoCategorias.estaVacio()) {
            System.out.println("No hay categorías registradas.");
            return;
        }
        int codigo = leerEntero("Ingrese el código de la categoría a eliminar: ");
        Categoria cat = repoCategorias.buscarPorCodigo(codigo);
        
        if (cat == null) {
            System.out.println("Categoría no encontrada.");
            return;
        }

        if (categoriaTieneArticulosAsociados(codigo)) {
            System.out.println("Error: No se puede eliminar la categoría porque hay artículos asociados a ella.");
            return;
        }

        repoCategorias.eliminar(cat);
        System.out.println("Categoría eliminada exitosamente.");
    }

    private boolean existeCategoriaPorNombre(String nombre) {
        for (Categoria c : repoCategorias.listar()) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    private boolean categoriaTieneArticulosAsociados(int codigoCategoria) {
        for (Articulo a : repoArticulos.listar()) {
            if (a.getCategoria().getCodigo() == codigoCategoria) {
                return true;
            }
        }
        return false;
    }
}
