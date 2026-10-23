import { useState } from 'react';
import Navbar from './components/Navbar.jsx';
import AcortarEnlace from './components/AcortarEnlace.jsx';
import VerEnlaces from './components/VerEnlaces.jsx';
import { consultarEnlaces } from './api/enlacesApi.js';

const CONSULTA_INICIAL = { cargando: false, error: null, datos: null, expiraEn: null };

export default function App() {
  const [modulo, setModulo] = useState('acortar');
  const [consulta, setConsulta] = useState(CONSULTA_INICIAL);

  // Cada clic en "Ver enlaces" es una consulta al backend. Se dispara desde el evento
  // (no desde un useEffect) para que nunca se hagan consultas duplicadas.
  async function consultar() {
    setConsulta((actual) => ({ ...actual, cargando: true, error: null }));
    try {
      const datos = await consultarEnlaces();
      const expiraEn = datos.desdeCache ? Date.now() + datos.ttlRestanteMs : null;
      setConsulta({ cargando: false, error: null, datos, expiraEn });
    } catch (error) {
      setConsulta({ ...CONSULTA_INICIAL, error: error.message });
    }
  }

  function abrirVerEnlaces() {
    setModulo('ver');
    consultar();
  }

  return (
    <>
      <Navbar modulo={modulo} onAcortar={() => setModulo('acortar')} onVerEnlaces={abrirVerEnlaces} />

      <main className="contenido">
        {modulo === 'acortar' ? (
          <AcortarEnlace />
        ) : (
          <VerEnlaces consulta={consulta} onConsultar={consultar} onIrAcortar={() => setModulo('acortar')} />
        )}
      </main>

      <footer className="pie">
        <span>atajo</span>
        <span>Spring Boot · Arquitectura hexagonal · MongoDB · Redis</span>
      </footer>
    </>
  );
}
