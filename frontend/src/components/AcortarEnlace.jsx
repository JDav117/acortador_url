import { useState } from 'react';
import { acortarEnlace } from '../api/enlacesApi.js';
import AccionesEnlace from './AccionesEnlace.jsx';
import EnlaceCorto from './EnlaceCorto.jsx';

export default function AcortarEnlace() {
  const [enlace, setEnlace] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState(null);
  const [resultado, setResultado] = useState(null);

  async function enviar(evento) {
    evento.preventDefault();
    setEnviando(true);
    setError(null);
    try {
      setResultado(await acortarEnlace(enlace));
      setEnlace('');
    } catch (e) {
      setError(e.message);
      setResultado(null);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="acortar">
      <div className="hero">
        <span className="sobretitulo">Acortador de enlaces</span>
        <h1>
          Enlaces largos,
          <br />
          caminos <em>cortos</em>.
        </h1>
        <p>Pega cualquier enlace y obtén un atajo de 8 caracteres listo para compartir.</p>
      </div>

      <form className="campo" onSubmit={enviar}>
        <label htmlFor="enlace" className="oculto">Enlace a acortar</label>
        <div className={error ? 'campo-grupo con-error' : 'campo-grupo'}>
          <svg className="campo-icono" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M10 14a4 4 0 0 0 5.66 0l3-3a4 4 0 0 0-5.66-5.66l-1 1" />
            <path d="M14 10a4 4 0 0 0-5.66 0l-3 3a4 4 0 0 0 5.66 5.66l1-1" />
          </svg>
          <input
            id="enlace"
            type="text"
            inputMode="url"
            autoComplete="off"
            spellCheck="false"
            placeholder="https://www.youtube.com/?gl=CO&hl=es-419"
            value={enlace}
            onChange={(e) => setEnlace(e.target.value)}
            aria-invalid={Boolean(error)}
            aria-describedby={error ? 'enlace-error' : undefined}
          />
          <button type="submit" className="btn btn-primario" disabled={enviando || !enlace.trim()}>
            {enviando ? 'Acortando…' : 'Acortar'}
          </button>
        </div>
        {error && (
          <p id="enlace-error" className="aviso aviso-error" role="alert">
            {error}
          </p>
        )}
      </form>

      {resultado && (
        <div className="resultado" aria-live="polite">
          <span className="resultado-etiqueta">Tu atajo está listo</span>
          <div className="resultado-fila">
            <EnlaceCorto url={resultado.enlaceAcortado} grande />
            <AccionesEnlace enlace={resultado.enlaceAcortado} />
          </div>
          <p className="resultado-original" title={resultado.enlaceOriginal}>
            <span aria-hidden="true">↳</span> {resultado.enlaceOriginal}
          </p>
        </div>
      )}

      <ul className="datos">
        <li>
          <strong>8</strong>
          <span>caracteres por código</span>
        </li>
        <li>
          <strong>302</strong>
          <span>redirección directa</span>
        </li>
        <li>
          <strong>Redis</strong>
          <span>caché del historial</span>
        </li>
      </ul>
    </section>
  );
}
