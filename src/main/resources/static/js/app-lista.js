
document.addEventListener('DOMContentLoaded', ()=> {

    const table = document.querySelector('#listaJuegos');
    if(!table) return;

    table.addEventListener('click', async (event)=> {

        const link = event.target.closest("a");

        const isDelete = link.classList.contains("borrarVideoJuegoLink");
        if(!isDelete) return;

        event.preventDefault();

        const tr = link.closest("tr");
        const idEl = tr && tr.querySelector(".videoJuegoId");
        const id = idEl ? idEl.textContent.trim() : null;

        const url = "/admin/videojuegos/" + id + "/delete/confirm";
        try{
            const response = await fetch(url);
            if(!response.ok) throw new Error(`Response status: ${response.status}`);

            const html = await response.text();
            document.querySelector('#placeholder-modal').innerHTML = html;

            const modalEl = document.querySelector('#delete-modal');
            if(modalEl){
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            }else{
                console.error("Modal no encontrado en el html recibido.");
            }
        } catch(error){
            console.error(error.message);
        }
    })

    const buscador = document.querySelector('#buscador');
    buscador.addEventListener('keyup', async () => {
        const url = "/admin/videojuegos/filter?";
        const queryParams = new URLSearchParams({numero: buscador.value}).toString();
        try {
            const response = await fetch(url + queryParams);
            if (!response.ok) throw new Error(`Response status: ${response.status}`);

            const html = await response.text();
            document.querySelector('#listaJuegos').innerHTML = html;
        } catch (error) {
            console.error(error.message);
        }
    })
});