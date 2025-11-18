-- Datos de prueba para el perfil de desarrollo
INSERT INTO proposals (title, description, start_date, end_date, is_active) VALUES 
('Hackathon', 'hacemos algo?', '2025-10-01 10:00:00', '2025-10-15 18:00:00', true),
('Implementación de Energía Solar', 'Propuesta para instalar paneles solares en el edificio principal', '2025-10-02 09:00:00', '2025-10-20 17:00:00', true),
('Reforma del Sistema de Biblioteca', 'Propuesta para digitalizar y modernizar el sistema de biblioteca', '2025-10-03 08:00:00', '2025-10-25 20:00:00', true),
('Propuesta Finalizada', 'Esta propuesta ya finalizó y debe mostrar votos', '2025-09-20 08:00:00', '2025-09-25 20:00:00', true),
('Propuesta Inactiva', 'Esta propuesta fue desactivada', '2025-10-01 10:00:00', '2025-10-15 18:00:00', false),
('Votación Abierta', 'Propuesta abierta para votar ahora mismo', '2025-09-27 08:00:00', '2025-09-30 23:59:59', true);

-- Datos de votos de prueba (todos con user_id como números sin comillas)
INSERT INTO votes (vote, user_id, proposal_id) VALUES 
('POSITIVE', 1, 1),
('POSITIVE', 2, 1),
('NEGATIVE', 3, 1),
('ABSTENCY', 4, 1),
('POSITIVE', 5, 2),
('NEGATIVE', 6, 2),
('POSITIVE', 7, 3),
('POSITIVE', 8, 3),
('POSITIVE', 9, 3),
('POSITIVE', 10, 4),
('POSITIVE', 11, 4),
('POSITIVE', 12, 4),
('NEGATIVE', 13, 4),
('NEGATIVE', 14, 4);