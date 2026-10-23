import { useEffect, useState } from 'react';

/**
 * Milisegundos que faltan para `expiraEn` (timestamp), actualizado varias veces por segundo.
 * El valor se calcula en cada render, así siempre corresponde al `expiraEn` actual.
 */
export function useCuentaRegresiva(expiraEn) {
  const [, setTic] = useState(0);

  useEffect(() => {
    if (!expiraEn) return undefined;
    const id = setInterval(() => {
      setTic((tic) => tic + 1);
      if (Date.now() >= expiraEn) clearInterval(id);
    }, 200);
    return () => clearInterval(id);
  }, [expiraEn]);

  return expiraEn ? Math.max(0, expiraEn - Date.now()) : 0;
}
