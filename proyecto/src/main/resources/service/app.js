async function obtenerUsuarios() {
    try {
        const response = await fetch('http://localhost:8080/api/usuarios');
        const usuarios = await response.json();
        console.log(usuarios);
    } catch (error) {
        console.error('Error al obtener usuarios:', error);
    }
}

obtenerUsuarios();