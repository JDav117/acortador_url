const BASE = '/api/enlaces';

async function leerRespuesta(respuesta) {
  if (!respuesta.ok) {
    let detalle;
    try {
      detalle = (await respuesta.json()).detail;
    } catch {
      // el cuerpo no era JSON
    }
    throw new Error(detalle ?? `Error ${respuesta.status} al comunicarse con el servidor`);
  }
  return respuesta.json();
}

export async function acortarEnlace(enlaceOriginal) {
  const respuesta = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ enlaceOriginal }),
  });
  return leerRespuesta(respuesta);
}

/** Devuelve { enlaces, fuente, desdeCache, ttlRestanteMs, ttlCacheMs }. */
export async function consultarEnlaces() {
  return leerRespuesta(await fetch(BASE));
}
