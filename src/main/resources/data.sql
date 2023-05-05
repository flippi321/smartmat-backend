-- Insert households
INSERT INTO household (name, invitation_nr) VALUES ('Smith Husholdning', 354674);
INSERT INTO household (name, invitation_nr) VALUES ('Johnson Husholdning', 847382);

-- Users
INSERT INTO _user (email, firstname, lastname, password, role, household_id) VALUES ('willsmith@gmail.com', 'Will', 'Smith', '$2a$10$jZGt3xHHbNJQiIDa4U8VW.NtiINYLvaFYiBPBsKBhRNMkng3gkrZO', 'USER', 1);
INSERT INTO _user (email, firstname, lastname, password, role, household_id) VALUES ('johnsmith@gmail.com', 'John', 'Smith', '$2a$10$jZGt3xHHbNJQiIDa4U8VW.NtiINYLvaFYiBPBsKBhRNMkng3gkrZO', 'USER', 1);
INSERT INTO _user (email, firstname, lastname, password, role, household_id) VALUES ('janesmith@gmail.com', 'Jane', 'Smith', '$2a$10$jZGt3xHHbNJQiIDa4U8VW.NtiINYLvaFYiBPBsKBhRNMkng3gkrZO', 'USER', 1);

INSERT INTO _user (email, firstname, lastname, password, role, household_id) VALUES ('robertjohnson@gmail.com', 'Robert', 'Johnson', '$2a$10$jZGt3xHHbNJQiIDa4U8VW.NtiINYLvaFYiBPBsKBhRNMkng3gkrZO', 'USER', 2);
INSERT INTO _user (email, firstname, lastname, password, role, household_id) VALUES ('emilyjohnson@gmail.com', 'Emily', 'Johnson', '$2a$10$jZGt3xHHbNJQiIDa4U8VW.NtiINYLvaFYiBPBsKBhRNMkng3gkrZO', 'USER', 2);


-- Insert fridges
INSERT INTO fridge (name, household_id) VALUES ('Kjøkkenkjøleskapet', 1);
INSERT INTO fridge (name, household_id) VALUES ('Kjøleskap', 2);

-- Insert category
INSERT INTO category (name, unit) VALUES ('Kjøtt', 'g');
INSERT INTO category (name, unit) VALUES ('Meieri', 'L');
INSERT INTO category (name, unit) VALUES ('Frukt og grønt', 'stk');
INSERT INTO category (name, unit) VALUES ('Kornprodukter', 'g');
INSERT INTO category (name, unit) VALUES ('Fisk og sjømat', 'g');
INSERT INTO category (name, unit) VALUES ('Annet', 'stk');
INSERT INTO category (name, unit) VALUES ('Hermetikk', 'stk');


-- Insert grocery items
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Melk', 7, 'https://www.lunsj.no/21397-large_default/tine-melk-lett.jpg', 2);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Egg', 30,'https://www.lunsj.no/20130-large_default/egg-fritgaende-honer-prior.jpg', 6);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Brød', 5, 'https://bilder.ngdata.no/2000254700006/meny/large.jpg', 4);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Eple', 30,  'https://www.lunsj.no/22685-large_default/okologiske-epler-braebrun.jpg', 3);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Banan', 7,'https://www.bamastorkjokken.no/globalassets/produktbilder-2/100/1301-32-main.jpg?width=250&height=250&quality=90&mode=Crop&anchor=Middlecenter&scale=Down&factor=1&backgroundColor=', 3);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Gulrøtt', 30,'https://bilder.kolonial.no/produkter/840c1ecf-12aa-427b-909c-226568c4f5de.jpg?auto=format&fit=max&w=500&s=05bff6f537c33ab4425279c066b9b251', 3);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Tomat', 7 ,'https://bilder.kolonial.no/local_products/d785a14a-c247-49a5-95a3-466060b1d498.jpg?auto=format&fit=max&w=500&s=0ed6f571960da62cb8e4cb6da68906a5', 3);
INSERT INTO grocery_item (name ,expected_shelf_life, image_link,category_id )VALUES ('Agurk' ,7 ,'https://engrosnett.no/image/frukt-og-gronnsaker/gronnsaker/1504-agurk.png?lb=true&v=638180975011870000' ,3 );
INSERT INTO grocery_item (name ,expected_shelf_life, image_link,category_id )VALUES ('Mais' ,365  ,'https://havaristen-i03.mycdn.no/mysimgprod/havaristen_mystore_no/images/eNnns_Green_Giant__kologisk_Mais_2x160g_1.jpg/w600h600.jpg' , 7);
INSERT INTO grocery_item (name ,expected_shelf_life ,image_link,category_id )VALUES ('Erter' ,365  ,'https://www.lunsj.no/16638-large_default/friske-erter-eldorado.jpg' , 7);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Bønner' ,365  ,'https://www.lunsj.no/19944-large_default/salat-og-grytebonner-goeco.jpg' , 7);
INSERT INTO grocery_item (name ,expected_shelf_life ,image_link,category_id )VALUES ('Havregryn' ,365 ,'https://www.lunsj.no/17227-large_default/okologisk-havregryn-stor-helios.jpg' , 4);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Pasta' ,730  ,'https://bilder.ngdata.no/7035620053566/kmh/large.jpg' , 4);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Ris' ,730  ,'https://bilder.ngdata.no/7311041016237/meny/large.jpg' , 4);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Laks' ,13  ,'https://bilder.ngdata.no/7033352672284/kiwi/large.jpg' , 5);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Kylling' ,14  ,'https://bilder.ngdata.no/7090013751535/meny/large.jpg' , 1);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Svinekjøtt' ,14  ,'https://bilder.kolonial.no/local_products/3e91cf78-4de8-49a8-8c63-7df8b32f32ce.png?auto=format&fit=max&w=500&s=3438af3de8c1feb97e5d26b2120fc4ac' , 1);
INSERT INTO grocery_item (name ,expected_shelf_life,image_link,category_id )VALUES ('Storfekjøtt' ,14  ,'https://engrosnett.no/image/kjottprodukter/storfekjott/2262897-2262897.png?lb=true' , 1);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Lam', 14, 'https://bilder.ngdata.no/2000769900007/meny/large.jpg', 1);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Kalkun', 14,  'https://res.cloudinary.com/norgesgruppen/image/upload/v1606951149/Product/2323257700003.jpg', 1);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Brokkoli', 14, 'https://engrosnett.no/image/frukt-og-gronnsaker/gronnsaker/2502-2502.jpg?lb=true&v=638181005813400000', 3);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Spinat', 7, 'https://www.lunsj.no/18990-large_default/spinat-puter-eldorado.jpg', 3);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Yoghurt', 14, 'https://bilder.kolonial.no/local_products/c750d06a-0aa3-4d75-928f-59246b8ec1bf.jpeg?auto=format&fit=max&w=330&s=b38f59cb178be5b274264c508427bea7', 2);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Ost', 30, 'https://www.lunsj.no/21389-large_default/tine-norvegia-ost.jpg', 6);
INSERT INTO grocery_item (name, expected_shelf_life, image_link, category_id) VALUES ('Appelsin', 21, 'https://www.lunsj.no/21018-large_default/okologisk-appelsin.jpg', 3);


-- Add grocery items to fridges
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 1, CURRENT_TIMESTAMP, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 2, CURRENT_TIMESTAMP, 30, CURRENT_DATE, CURRENT_DATE + INTERVAL '30' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 3, CURRENT_TIMESTAMP, 2,CURRENT_DATE ,CURRENT_DATE + INTERVAL '5' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 4,CURRENT_TIMESTAMP , 6,CURRENT_DATE ,CURRENT_DATE + INTERVAL '30' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (1 ,5 ,CURRENT_TIMESTAMP , 8 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 6, CURRENT_TIMESTAMP, 7,CURRENT_DATE ,CURRENT_DATE + INTERVAL '30' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 7, CURRENT_TIMESTAMP, 9,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 8, CURRENT_TIMESTAMP , 2,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (1 , 9, CURRENT_TIMESTAMP ,3 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '365' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 16, CURRENT_TIMESTAMP, 1000,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 17, CURRENT_TIMESTAMP , 800,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (1, 18, CURRENT_TIMESTAMP ,300 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '365' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 13, CURRENT_TIMESTAMP, 850,CURRENT_DATE ,CURRENT_DATE + INTERVAL '365' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (1, 24, CURRENT_TIMESTAMP , 2,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (1, 14, CURRENT_TIMESTAMP ,1 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '12' DAY);

INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (2, 1, CURRENT_TIMESTAMP, 3,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp, amount, purchase_date, expiration_date) VALUES (2, 2,CURRENT_TIMESTAMP , 15,CURRENT_DATE ,CURRENT_DATE + INTERVAL '30' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (2 ,3 ,CURRENT_TIMESTAMP ,4 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '5' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (2 ,4 ,CURRENT_TIMESTAMP ,8 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '30' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (2 ,5 ,CURRENT_TIMESTAMP ,8 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '7' DAY);
INSERT INTO grocery_item_fridge (fridge_id, grocery_item_id, timestamp ,amount ,purchase_date ,expiration_date) VALUES (2 ,6 ,CURRENT_TIMESTAMP ,2 ,CURRENT_DATE ,CURRENT_DATE + INTERVAL '30' DAY);

INSERT INTO recipe (name, description, image_link) VALUES ('Eggerøre', 'En enkel og deilig frokostrett laget med egg, melk og smør.', 'https://www.rema.no/wordpress/wp-content/uploads/2020/03/Silkemyk-eggerore_w1280hq75.jpeg');
INSERT INTO recipe (name, description, image_link) VALUES ('Havregrøt', 'En sunn og mettende frokost laget med havregryn, melk eller vann og en klype salt.', 'https://res.cloudinary.com/norgesgruppen/images/c_scale,dpr_auto,f_auto,q_auto:eco,w_1600/onsmjnq4pv79gcjo9mwi/hjemmelaget-havregrot');
INSERT INTO recipe (name, description, image_link) VALUES ('Pasta med tomatsaus', 'En enkel og smakfull middagsrett laget med pasta, hermetiske tomater, hvitløk og urter.', 'https://www.rema.no/wordpress/wp-content/uploads/2022/05/140326_U100Juni_036_crop_27487_psd.jpg');
INSERT INTO recipe (name, description, image_link) VALUES ('Kyllingsalat', 'En frisk og sunn lunsjrett laget med kylling, salatblader, agurk, tomat og en god dressing.', 'https://www.bama.no/siteassets/fotoware/2022/12/gf_salater_jan_23-4042.jpg?width=750&height=750&mode=crop');
INSERT INTO recipe (name, description, image_link) VALUES ('Laksefilet med grønnsaker', 'En sunn og enkel middagsrett laget med laksefilet, brokkoli, gulrøtter og poteter.', 'https://res.cloudinary.com/norgesgruppen/images/c_scale,dpr_auto,f_auto,q_auto:eco,w_1600/zktci42krttzkbfe4axt/ovnsbakt-laksefilet-med-brokkoli-og-tomater');
INSERT INTO recipe (name, description, image_link) VALUES ('Grønnsakssuppe', 'En varmende og næringsrik suppe laget med gulrøtter, brokkoli, løk og buljong.', 'https://www.bama.no/contentassets/8228f06bed034191b674a4671b04584a/21a5096184a74180836c260a5f2c27462.jpg');
INSERT INTO recipe (name, description, image_link) VALUES ('Yoghurt med frukt og nøtter', 'En sunn og deilig snack laget med yoghurt, oppskåret frukt og hakkede nøtter.', 'https://www.rema.no/wordpress/wp-content/uploads/2022/05/REC-20227-1600x1080.jpg');
INSERT INTO recipe (name, description, image_link) VALUES ('Omelett med ost og skinke', 'En mettende og smakfull frokost- eller lunsjrett laget med egg, ost, skinke og urter.', 'https://images.matprat.no/4cm2xgsmea-jumbotron/large');
INSERT INTO recipe (name, description, image_link) VALUES ('Frukt- og bærsalat', 'En frisk og sunn dessert laget med oppskåret frukt og bær, servert med en klatt yoghurt eller vaniljekesam.', 'https://s1.1zoom.me/big0/382/Salads_Fruit_Berry_496338.jpg');
INSERT INTO recipe (name, description, image_link) VALUES ('Grønnsakswok', 'En enkel og smakfull middagsrett laget med oppskåret grønnsaker, nudler og en god woksaus.', 'https://www.bama.no/siteassets/fotoware/2020/11/bama-bare-wokmix-2338.jpg?width=750&height=750&mode=crop');

-- For the Eggerøre recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (1, 2, 4); -- 4 eggs
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (1, 1, 0.25); -- 0.25L of milk

-- For the Havregrøt recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (2, 12, 250); -- 250g of havregryn
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (2, 1, 0.4); -- 0.4L of milk or water

-- For the Pasta med tomatsaus recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (3, 13, 100); -- 100g of pasta
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (3, 7, 2); -- 2 tomatoes

-- For the Kyllingsalat recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (4, 16, 100); -- 100g of cooked chicken
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (4, 8, 0.5); -- half a cucumber
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (4, 7, 1); -- 1 tomato

-- For the Laksefilet med grønnsaker recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (5, 15, 150); -- 150g of salmon fillet
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (5, 21, 2); -- 2 broccoli
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (5, 6, 3); -- 3 spinat

-- For the Grønnsakssuppe recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (6, 6, 6); -- 6 carrots
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (6, 21, 4); -- 4 broccoli
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (6, 22, 3); -- 1 spinat

-- Yoghurt med frukt og nøtter recipe
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (7, 24, 0.25); -- 0.25L of yoghurt
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (7, 4, 0.5); -- half an apple
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (7, 5, 0.5); -- half a banana

-- For the Omelett med ost og skinke recipe:
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (8, 2, 3); -- 3 eggs
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (8, 25, 1); -- 1 cheese
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (8, 17, 50); -- 50g of ham

-- For the Frukt- og bærsalat recipe:
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (9, 4, 0.5); -- half an apple
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (9, 5, 0.5); -- half a banana
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (9, 24, 0.15); -- 0.15L yoghurt or vaniljekesam

-- For the Grønnsakswok recipe:
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (10, 6, 2); -- 2 carrots
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (10, 22, 2); -- 2 broccoli
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (10, 8, 0.5); -- half a cucumber
INSERT INTO grocery_item_recipe (recipe_id, grocery_item_id, amount) VALUES (10, 14, 100); -- 100g ris


-- Insert shoppinglist
INSERT INTO shoppinglist (name, household_id) VALUES ('Shoppinglist1', 1);
INSERT INTO shoppinglist (name, household_id) VALUES ('Shoppinglist2', 2);

-- Add last 6 grocery items to Shoppinglist1
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (16, 1, CURRENT_TIMESTAMP, 14, 250);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (17, 1, CURRENT_TIMESTAMP, 14, 500);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (18 ,1 ,CURRENT_TIMESTAMP , 14,600);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (19 ,1 ,CURRENT_TIMESTAMP , 14,750);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (20 ,1 ,CURRENT_TIMESTAMP , 14,100);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (21 ,1 ,CURRENT_TIMESTAMP , 14,350);

-- Add last 3 grocery items to Shoppinglist2
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (19 ,2 ,CURRENT_TIMESTAMP , 14,200);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (20 ,2 ,CURRENT_TIMESTAMP , 14,300);
INSERT INTO grocery_item_shoppinglist (grocery_item_id, shoppinglist_id, timestamp, actual_shelf_life, amount) VALUES (21 ,2 ,CURRENT_TIMESTAMP , 14,100);

-- Eggerøre
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (1, 'I en bolle, visp sammen egg, melk, salt og pepper.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (1, 'Varm smør i en non-stick stekepanne over middels varme.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (1, 'Hell eggeblandingen i stekepannen og kok, rør av og til, til eggene er stekt.', 2);

-- Havregrøt
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (2, 'Ha havregryn og melk eller vann i en kjele med en klype salt.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (2, 'Kok opp under omrøring og la grøten småkoke i ca. 5 minutter.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (2, 'Server grøten varm med ønsket tilbehør.', 2);

-- Pasta med tomatsaus
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (3, 'Kok pastaen etter anvisning på pakken.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (3, 'Varm olje i en stekepanne over middels varme. Tilsett hakket hvitløk og stek i ca. 1 minutt.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (3, 'Tilsett hermetiske tomater og urter. La sausen småkoke i ca. 10 minutter. Smak til med salt og pepper.', 2);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (3, 'Server pastaen med tomatsausen over.', 3);

-- Kyllingsalat
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (4, 'Skjær kylling i strimler og stek i en stekepanne til gjennomstekt.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (4, 'Skyll salatbladene og legg dem på en tallerken.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (4, 'Skjær agurk og tomat i skiver og legg på salatbladene.', 2);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (4, 'Legg kyllingstrimlene på toppen og server med en god dressing.', 3);

-- Laksefilet med grønnsaker
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (5, 'Forvarm ovnen til 200 grader.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (5, 'Legg laksefiletene i en ildfast form og krydre med salt og pepper. Stek i ovnen i ca. 15 minutter.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (5, 'Kok brokkoli og gulrøtter i lettsaltet vann til de er møre.', 2);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (5, 'Kok potetene i lettsaltet vann til de er møre.', 3);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (5, 'Server laksefiletene med grønnsakene og potetene ved siden av.', 4);

-- Grønnsakssuppe
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (6, 'Skrell og skjær gulrøtter og løk i biter. Stek i en kjele med litt olje til løken er myk.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (6, 'Tilsett buljong og vann. Kok opp og la småkoke i ca. 10 minutter.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (6, 'Skjær brokkoli i små buketter og tilsett i suppen. La småkoke i ytterligere 5 minutter.', 2);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (6, 'Smak til med salt og pepper. Server suppen varm med brød ved siden av.', 3);

-- Yoghurt med frukt og nøtter
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (7, 'Ha yoghurt i en skål.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (7, 'Skjær opp frukt og bær og legg på toppen av yoghurten.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (7, 'Hakk nøtter og dryss over. Server straks.', 2);

-- Omelett med ost og skinke
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (8, 'I en bolle, visp sammen egg, salt og pepper.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (8, 'Varm smør i en non-stick stekepanne over middels varme.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (8, 'Hell eggeblandingen i stekepannen og la den stivne på undersiden.', 2);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (8, 'Legg ost og skinke på halve omeletten og brett den andre halvdelen over. La omeletten steke til osten er smeltet.', 3);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (8, 'Server omeletten varm med urter på toppen.', 4);

-- Frukt- og bærsalat
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (9, 'Skjær opp frukt og bær i små biter og legg i en skål.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (9, 'Server salaten med en klatt yoghurt eller vaniljekesam på toppen.', 1);

-- Grønnsakswok
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (10, 'Kok nudlene etter anvisning på pakken og skyll i kaldt vann.', 0);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (10, 'Varm olje i en wokpanne eller stor stekepanne over høy varme.', 1);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (10, 'Tilsett oppskåret grønnsaker og stek under omrøring i ca. 5 minutter.', 2);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (10, 'Tilsett nudler og woksaus og stek i ytterligere 2 minutter.', 3);
INSERT INTO recipe_steps (recipe_id, step, step_order) VALUES (10, 'Server grønnsakswoken varm.', 4);
