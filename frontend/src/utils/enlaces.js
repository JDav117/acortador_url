/** Separa un enlace en dominio y resto, para resaltar el dominio. */
export function separarEnlace(enlace) {
  try {
    const url = new URL(enlace);
    const resto = `${url.pathname === '/' ? '' : url.pathname}${url.search}${url.hash}`;
    return { dominio: url.host.replace(/^www\./, ''), resto };
  } catch {
    return { dominio: enlace, resto: '' };
  }
}

/** "http://localhost:8080/0440E65L" → { base: "localhost:8080/", codigo: "0440E65L" } */
export function separarAtajo(enlaceAcortado) {
  const sinEsquema = enlaceAcortado.replace(/^https?:\/\//, '');
  const corte = sinEsquema.lastIndexOf('/') + 1;
  return { base: sinEsquema.slice(0, corte), codigo: sinEsquema.slice(corte) };
}
