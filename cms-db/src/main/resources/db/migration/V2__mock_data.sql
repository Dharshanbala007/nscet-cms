-- V2: Mock data for NSCET CMS
-- Department IDs: 1=MECH,2=CE,3=ECE,4=CSE,5=ACCT,6=ADMIN,7=SYS,8=TRANS,9=LIB,10=CAN,11=PHY,12=CHEM,13=MATH,14=PD,15=ENG,16=TPO,17=SH,18=EEE,19=ME,20=IT,21=AI,22=SE,23=TAM,24=EST,25=MECS
-- Designation IDs: 1=AP,2=HOD,3=Associate Professor,4=Library,5=PD,6=P.Co-ordinator,7=Teaching Fellow,8=Dept Incharge,9=EOC

-- STAFF DATA
INSERT INTO admin_staff_master (staff_code, name, address, city, pin_code, date_of_birth, category, department_id, designation_id, staff_group, college_code, transport, email, pf_active, sex, date_of_joining, phone, blood_group, aadhar_number) VALUES
-- CSE Department (dept_id=4)
('STF001', 'Dr. R. Velraj', '12/A, College Road', 'Theni', '625531', '1972-05-14', 'OC', 4, 2, 'Teaching', 'NSCET', 'College', 'velraj@nscet.edu', TRUE, 'M', '2005-06-15', '9876543201', 'B+', '123456789001'),
('STF002', 'Dr. S. Kannan', '45, Kamaraj Salai', 'Theni', '625531', '1978-03-22', 'BC', 4, 3, 'Teaching', 'NSCET', 'College', 'kannan@nscet.edu', TRUE, 'M', '2008-07-01', '9876543202', 'O+', '123456789002'),
('STF003', 'Mr. K. Murugan', '78, MG Road', 'Theni', '625531', '1985-08-10', 'MBC', 4, 1, 'Teaching', 'NSCET', 'College', 'murugan@nscet.edu', TRUE, 'M', '2012-01-15', '9876543203', 'A+', '123456789003'),
('STF004', 'Ms. P. Deepa', '23, Nehru Nagar', 'Theni', '625531', '1990-11-25', 'OC', 4, 1, 'Teaching', 'NSCET', 'Own', 'deepa@nscet.edu', TRUE, 'F', '2016-06-01', '9876543204', 'B-', '123456789004'),
('STF005', 'Mr. T. Senthil', '56, VOC Street', 'Theni', '625531', '1988-04-18', 'SC', 4, 1, 'Teaching', 'NSCET', 'College', 'senthil@nscet.edu', TRUE, 'M', '2014-08-01', '9876543205', 'AB+', '123456789005'),
-- ECE Department (dept_id=3)
('STF006', 'Dr. M. Palani', '9, Gandhi Road', 'Theni', '625531', '1975-01-30', 'OC', 3, 2, 'Teaching', 'NSCET', 'College', 'palani@nscet.edu', TRUE, 'M', '2006-06-15', '9876543206', 'O-', '123456789006'),
('STF007', 'Dr. V. Lakshmi', '34, Ambedkar Nagar', 'Theni', '625531', '1980-07-12', 'BC', 3, 3, 'Teaching', 'NSCET', 'Own', 'lakshmi@nscet.edu', TRUE, 'F', '2009-01-05', '9876543207', 'B+', '123456789007'),
('STF008', 'Mr. B. Karthik', '67, Periyar Street', 'Theni', '625531', '1987-09-05', 'MBC', 3, 1, 'Teaching', 'NSCET', 'College', 'karthik@nscet.edu', TRUE, 'M', '2015-07-01', '9876543208', 'A-', '123456789008'),
-- MECH Department (dept_id=1)
('STF009', 'Dr. G. Rajendran', '88, Kumaran Nagar', 'Theni', '625531', '1970-12-08', 'OC', 1, 2, 'Teaching', 'NSCET', 'College', 'rajendran@nscet.edu', TRUE, 'M', '2002-06-15', '9876543209', 'O+', '123456789009'),
('STF010', 'Mr. S. Prakash', '15, Annai Nagar', 'Theni', '625531', '1983-06-20', 'BC', 1, 1, 'Teaching', 'NSCET', 'College', 'prakash@nscet.edu', TRUE, 'M', '2011-01-15', '9876543210', 'B+', '123456789010'),
-- CE Department (dept_id=2)
('STF011', 'Dr. N. Mohan', '42, Lakshmi Puram', 'Theni', '625531', '1976-10-15', 'OC', 2, 2, 'Teaching', 'NSCET', 'College', 'mohan@nscet.edu', TRUE, 'M', '2007-06-15', '9876543211', 'A+', '123456789011'),
('STF012', 'Ms. R. Priya', '19, Saravana Nagar', 'Theni', '625531', '1991-02-28', 'OC', 2, 1, 'Teaching', 'NSCET', 'Own', 'priya@nscet.edu', TRUE, 'F', '2018-01-15', '9876543212', 'B+', '123456789012'),
-- EEE Department (dept_id=18)
('STF013', 'Dr. A. Balaji', '55, Rajaji Nagar', 'Theni', '625531', '1974-08-22', 'OC', 18, 2, 'Teaching', 'NSCET', 'College', 'balaji@nscet.edu', TRUE, 'M', '2004-06-15', '9876543213', 'O+', '123456789013'),
('STF014', 'Mr. J. Venkatesh', '31, Thiruvalluvar Street', 'Theni', '625531', '1986-05-03', 'BC', 18, 1, 'Teaching', 'NSCET', 'College', 'venkatesh@nscet.edu', TRUE, 'M', '2013-07-01', '9876543214', 'AB-', '123456789014'),
-- IT Department (dept_id=20)
('STF015', 'Dr. C. Suresh', '27, Bharathi Nagar', 'Theni', '625531', '1979-11-11', 'MBC', 20, 2, 'Teaching', 'NSCET', 'College', 'suresh@nscet.edu', TRUE, 'M', '2010-01-05', '9876543215', 'B-', '123456789015'),
('STF016', 'Ms. D. Kavitha', '63, MGR Nagar', 'Theni', '625531', '1992-07-19', 'SC', 20, 1, 'Teaching', 'NSCET', 'Own', 'kavitha@nscet.edu', TRUE, 'F', '2019-06-01', '9876543216', 'A+', '123456789016'),
-- AI Department (dept_id=21)
('STF017', 'Dr. K. Mohanasundaram', '18, Sivan Kovil Street', 'Theni', '625531', '1981-04-07', 'OC', 21, 2, 'Teaching', 'NSCET', 'College', 'mohanasundaram@nscet.edu', TRUE, 'M', '2011-06-15', '9876543217', 'O+', '123456789017'),
-- Accounts Department (dept_id=5)
('STF018', 'Mr. V. Senthilkumar', '91, Kamaraj Nagar', 'Theni', '625531', '1982-09-14', 'BC', 5, 1, 'Office', 'NSCET', 'College', 'senthilkumar.admin@nscet.edu', TRUE, 'M', '2010-03-01', '9876543218', 'B+', '123456789018'),
('STF019', 'Mrs. S. Revathi', '44, Anna Nagar', 'Theni', '625531', '1985-12-25', 'OC', 5, 1, 'Office', 'NSCET', 'Own', 'revathi@nscet.edu', TRUE, 'F', '2013-06-15', '9876543219', 'AB+', '123456789019'),
-- Library (dept_id=9)
('STF020', 'Mr. M. Rajendran', '72, Jothi Nagar', 'Theni', '625531', '1978-03-08', 'MBC', 9, 4, 'Office', 'NSCET', 'College', 'rajendran.m@nscet.edu', FALSE, 'M', '2008-01-15', '9876543220', 'O+', '123456789020'),
-- Canteen (dept_id=10)
('STF021', 'Mr. P. Kumar', '36, Sakthi Nagar', 'Theni', '625531', '1980-06-17', 'SC', 10, 7, 'Office', 'NSCET', 'Own', 'kumar.gardener@nscet.edu', FALSE, 'M', '2009-08-01', '9876543221', 'B-', '123456789021'),
-- Transport (dept_id=8)
('STF022', 'Mr. R. Manikandan', '58, Vellakinaru', 'Theni', '625531', '1975-08-30', 'MBC', 8, 7, 'Office', 'NSCET', 'College', 'manikandan@nscet.edu', FALSE, 'M', '2005-04-01', '9876543222', 'A-', '123456789022'),
-- Admin (dept_id=6)
('STF023', 'Mr. S. Baskar', '29, VOC Colony', 'Theni', '625531', '1983-02-14', 'BC', 6, 7, 'Office', 'NSCET', 'College', 'baskar@nscet.edu', FALSE, 'M', '2011-09-01', '9876543223', 'O+', '123456789023'),
-- TPO (dept_id=16)
('STF024', 'Mrs. K. Meenakshi', '81, Gandhi Maidan', 'Theni', '625531', '1987-10-21', 'OC', 16, 6, 'Office', 'NSCET', 'Own', 'meenakshi@nscet.edu', TRUE, 'F', '2014-01-15', '9876543224', 'B+', '123456789024'),
-- SysAdmin (dept_id=7)
('STF025', 'Mr. D. Ilango', '50, Subramaniapuram', 'Theni', '625531', '1979-05-09', 'BC', 7, 5, 'Office', 'NSCET', 'College', 'ilango@nscet.edu', TRUE, 'M', '2007-06-15', '9876543225', 'AB-', '123456789025');

-- ============================
-- STUDENT DATA (CSE Department - 40 students)
-- ============================
INSERT INTO admin_student_master (roll_number, registration_no, admission_no, name, father_name, mother_name, phone, parent_phone, gender, aadhar_number, date_of_birth, community, caste, region, city, email, address, blood_group, medium, bus_stop, transport_type, state, admission_type) VALUES
('23CSE001', 'REG23001', 'ADM23001', 'Arun Kumar S', 'Subramani S', 'Lakshmi S', '9876500001', '9876500051', 'M', '200000000001', '2005-03-15', 'OC', 'Gounder', 'Theni', 'Theni', 'arun@student.nscet.edu', '12, North Street, Theni', 'O+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE002', 'REG23002', 'ADM23002', 'Priya M', 'Murugan M', 'Kala M', '9876500002', '9876500052', 'F', '200000000002', '2005-07-22', 'BC', 'Thevar', 'Theni', 'Theni', 'priya@student.nscet.edu', '34, South Avenue, Theni', 'B+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE003', 'REG23003', 'ADM23003', 'Rajesh K', 'Krishnan K', 'Parvathi K', '9876500003', '9876500053', 'M', '200000000003', '2005-01-10', 'MBC', 'MBC', 'Madurai', 'Madurai', 'rajesh@student.nscet.edu', '56, KK Nagar, Madurai', 'A+', 'English', 'Madurai Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE004', 'REG23004', 'ADM23004', 'Sneha R', 'Raman R', 'Meena R', '9876500004', '9876500054', 'F', '200000000004', '2005-09-05', 'OC', 'Nair', 'Kerala', 'Kochi', 'sneha@student.nscet.edu', '78, Marine Drive, Kochi', 'B-', 'English', 'Kochi Counter', 'Self', 'Kerala', 'Management'),
('23CSE005', 'REG23005', 'ADM23005', 'Karthik T', 'Thirumalai T', 'Annalakshmi T', '9876500005', '9876500055', 'M', '200000000005', '2004-12-28', 'SC', 'SC', 'Theni', 'Periyakulam', 'karthik@student.nscet.edu', '90, Temple Road, Periyakulam', 'O-', 'English', 'Periyakulam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE006', 'REG23006', 'ADM23006', 'Divya S', 'Sundar S', 'Kamala S', '9876500006', '9876500056', 'F', '200000000006', '2005-04-18', 'OC', 'Nadar', 'Theni', 'Bodinayakanur', 'divya@student.nscet.edu', '23, Market Road, Bodinayakanur', 'AB+', 'Tamil', 'Bodinayakanur Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE007', 'REG23007', 'ADM23007', 'Vignesh P', 'Palani P', 'Saroja P', '9876500007', '9876500057', 'M', '200000000007', '2005-06-12', 'BC', 'MBC', 'Theni', 'Uthamapalayam', 'vignesh@student.nscet.edu', '45, Lake View, Uthamapalayam', 'B+', 'English', 'Uthamapalayam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE008', 'REG23008', 'ADM23008', 'Anitha V', 'Venkatesh V', 'Sumathi V', '9876500008', '9876500058', 'F', '200000000008', '2005-02-14', 'OC', 'Reddy', 'Andhra Pradesh', 'Hyderabad', 'anitha@student.nscet.edu', '67, Banjara Hills, Hyderabad', 'A-', 'English', 'Hyderabad Counter', 'Self', 'Andhra Pradesh', 'Management'),
('23CSE009', 'REG23009', 'ADM23009', 'Mohan C', 'Chinnasamy C', 'Angammal C', '9876500009', '9876500059', 'M', '200000000009', '2004-11-30', 'MBC', 'MBC', 'Theni', 'Cumbum', 'mohan@student.nscet.edu', '12, Hill Top, Cumbum', 'O+', 'Tamil', 'Cumbum Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE010', 'REG23010', 'ADM23010', 'Fathima N', 'Nasser N', 'Ayesha N', '9876500010', '9876500060', 'F', '20000000010', '2005-08-08', 'BC', 'Muslim', 'Theni', 'Theni', 'fathima@student.nscet.edu', '33, Mosque Street, Theni', 'AB+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('24CSE001', 'REG24001', 'ADM24001', 'Vikram R', 'Rajan R', 'Geetha R', '9876500101', '9876500151', 'M', '20000000011', '2006-01-20', 'OC', 'Gounder', 'Theni', 'Theni', 'vikram@student.nscet.edu', '88, Rajaji Nagar, Theni', 'B+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('24CSE002', 'REG24002', 'ADM24002', 'Kavitha L', 'Loganathan L', 'Selvi L', '9876500102', '9876500152', 'F', '20000000012', '2006-05-14', 'BC', 'Thevar', 'Madurai', 'Madurai', 'kavitha@student.nscet.edu', '22, Anna Nagar, Madurai', 'O+', 'English', 'Madurai Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('24CSE003', 'REG24003', 'ADM24003', 'Saravanan M', 'Murugesan M', 'Ponnathai M', '9876500103', '9876500153', 'M', '20000000013', '2006-03-25', 'SC', 'SC', 'Theni', 'Theni', 'saravanan@student.nscet.edu', '56, Ambedkar Nagar, Theni', 'A+', 'Tamil', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('24CSE004', 'REG24004', 'ADM24004', 'Nandhini K', 'Krishna K', 'Meenakshi K', '9876500104', '9876500154', 'F', '20000000014', '2006-07-08', 'OC', 'Brahmin', 'Tamil Nadu', 'Chennai', 'nandhini@student.nscet.edu', '34, T Nagar, Chennai', 'B-', 'English', 'Chennai Counter', 'Self', 'Tamil Nadu', 'Management'),
('24CSE005', 'REG24005', 'ADM24005', 'Bharathi S', 'Siva S', 'Lakshmi S', '9876500105', '9876500155', 'M', '20000000015', '2006-09-19', 'MBC', 'MBC', 'Theni', 'Periyakulam', 'bharathi@student.nscet.edu', '78, Station Road, Periyakulam', 'O-', 'English', 'Periyakulam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23ECE001', 'REG23101', 'ADM23101', 'Deepak R', 'Ramasamy R', 'Kamatchi R', '9876500201', '9876500251', 'M', '20000000016', '2005-04-12', 'OC', 'Nadar', 'Theni', 'Theni', 'deepak@student.nscet.edu', '45, College Road, Theni', 'B+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23ECE002', 'REG23102', 'ADM23102', 'Swetha P', 'Paramasivam P', 'Lakshmi P', '9876500202', '9876500252', 'F', '20000000017', '2005-08-26', 'BC', 'Thevar', 'Theni', 'Bodinayakanur', 'swetha@student.nscet.edu', '12, Temple Street, Bodinayakanur', 'A-', 'English', 'Bodinayakanur Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23ECE003', 'REG23103', 'ADM23103', 'Praveen K', 'Kumar K', 'Saroja K', '9876500203', '9876500253', 'M', '20000000018', '2005-02-08', 'MBC', 'MBC', 'Madurai', 'Madurai', 'praveen@student.nscet.edu', '67, KK Nagar, Madurai', 'AB+', 'English', 'Madurai Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23ECE004', 'REG23104', 'ADM23104', 'Hema T', 'Thangam T', 'Kamala T', '9876500204', '9876500254', 'F', '20000000019', '2005-11-15', 'OC', 'Gounder', 'Theni', 'Uthamapalayam', 'hema@student.nscet.edu', '89, Hill Road, Uthamapalayam', 'B-', 'English', 'Uthamapalayam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23MECH001', 'REG23201', 'ADM23201', 'Suresh Babu R', 'Rajan R', 'Saroja R', '9876500301', '9876500351', 'M', '20000000020', '2005-06-06', 'OC', 'Nadar', 'Theni', 'Theni', 'suresh.mech@student.nscet.edu', '23, West Car Street, Theni', 'O+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23MECH002', 'REG23202', 'ADM23202', 'Karthikeyan S', 'Subbaiah S', 'Ponnammal S', '9876500302', '9876500352', 'M', '20000000021', '2005-10-20', 'BC', 'Thevar', 'Theni', 'Periyakulam', 'karthikeyan@student.nscet.edu', '56, Main Road, Periyakulam', 'A+', 'Tamil', 'Periyakulam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23EEE001', 'REG23301', 'ADM23301', 'Ashok V', 'Venugopal V', 'Saroja V', '9876500401', '9876500451', 'M', '20000000022', '2005-05-18', 'OC', 'Gounder', 'Theni', 'Theni', 'ashok@student.nscet.edu', '34, Power House Road, Theni', 'B+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23EEE002', 'REG23302', 'ADM23302', 'Revathi M', 'Muthu M', 'Kamatchi M', '9876500402', '9876500452', 'F', '20000000023', '2005-09-14', 'BC', 'MBC', 'Theni', 'Cumbum', 'revathi@student.nscet.edu', '78, Lake Road, Cumbum', 'O-', 'English', 'Cumbum Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23IT001', 'REG23401', 'ADM23401', 'Murali Krishna D', 'Duraisamy D', 'Parvathi D', '9876500501', '9876500551', 'M', '20000000024', '2005-03-03', 'OC', 'Nadar', 'Theni', 'Theni', 'murali@student.nscet.edu', '90, Anna Salai, Theni', 'AB+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23IT002', 'REG23402', 'ADM23402', 'Jayashree B', 'Balasubramanian B', 'Meenakshi B', '9876500502', '9876500552', 'F', '20000000025', '2005-07-29', 'OC', 'Brahmin', 'Tamil Nadu', 'Coimbatore', 'jayashree@student.nscet.edu', '12, RS Puram, Coimbatore', 'B-', 'English', 'Coimbatore Counter', 'Self', 'Tamil Nadu', 'Management'),
('23AI001', 'REG23501', 'ADM23501', 'Adhitya R', 'Rajendar R', 'Kala R', '9876500601', '9876500651', 'M', '20000000026', '2005-01-25', 'OC', 'Gounder', 'Theni', 'Theni', 'adhitya@student.nscet.edu', '56, Tech Park Road, Theni', 'O+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23AI002', 'REG23502', 'ADM23502', 'Nivetha S', 'Senthil S', 'Kamala S', '9876500602', '9876500652', 'F', '20000000027', '2005-06-17', 'BC', 'Thevar', 'Theni', 'Bodinayakanur', 'nivetha@student.nscet.edu', '34, Gandhi Street, Bodinayakanur', 'A+', 'English', 'Bodinayakanur Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE011', 'REG23011', 'ADM23011', 'Thirumalai M', 'Mani M', 'Rathinam M', '9876500011', '9876500061', 'M', '20000000028', '2005-01-05', 'MBC', 'MBC', 'Theni', 'Theni', 'thirumalai@student.nscet.edu', '45, West Car Street, Theni', 'B+', 'Tamil', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE012', 'REG23012', 'ADM23012', 'Shalini P', 'Pandian P', 'Lakshmi P', '9876500012', '9876500062', 'F', '20000000029', '2005-05-20', 'OC', 'Nadar', 'Theni', 'Uthamapalayam', 'shalini@student.nscet.edu', '67, Market Road, Uthamapalayam', 'O-', 'English', 'Uthamapalayam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE013', 'REG23013', 'ADM23013', 'Dinesh K', 'Kandasamy K', 'Ponnammal K', '9876500013', '9876500063', 'M', '20000000030', '2004-12-10', 'SC', 'SC', 'Theni', 'Periyakulam', 'dinesh@student.nscet.edu', '78, Ambedkar Nagar, Periyakulam', 'A-', 'Tamil', 'Periyakulam Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE014', 'REG23014', 'ADM23014', 'Keerthana V', 'Viswanathan V', 'Saroja V', '9876500014', '9876500064', 'F', '20000000031', '2005-08-30', 'OC', 'Brahmin', 'Tamil Nadu', 'Chennai', 'keerthana@student.nscet.edu', '12, Mylapore, Chennai', 'AB+', 'English', 'Chennai Counter', 'Self', 'Tamil Nadu', 'Management'),
('23CSE015', 'REG23015', 'ADM23015', 'Prasanth E', 'Elangovan E', 'Kamala E', '9876500015', '9876500065', 'M', '20000000032', '2005-04-02', 'BC', 'MBC', 'Theni', 'Cumbum', 'prasanth@student.nscet.edu', '90, Hill View, Cumbum', 'B+', 'Tamil', 'Cumbum Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE016', 'REG23016', 'ADM23016', 'Sabapathi N', 'Nallasamy N', 'Angammal N', '9876500016', '9876500066', 'M', '20000000033', '2005-10-14', 'OC', 'Gounder', 'Theni', 'Theni', 'sabapathi@student.nscet.edu', '23, Anna Nagar, Theni', 'O+', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE017', 'REG23017', 'ADM23017', 'Gayathri S', 'Sundaram S', 'Padmini S', '9876500017', '9876500067', 'F', '20000000034', '2005-12-22', 'OC', 'Nadar', 'Theni', 'Theni', 'gayathri@student.nscet.edu', '56, West Car Street, Theni', 'B-', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE018', 'REG23018', 'ADM23018', 'Karthi S', 'Sakthivel S', 'Meenakshi S', '9876500018', '9876500068', 'M', '20000000035', '2005-02-18', 'BC', 'Thevar', 'Madurai', 'Madurai', 'karthi@student.nscet.edu', '78, KK Nagar, Madurai', 'A+', 'English', 'Madurai Stop', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE019', 'REG23019', 'ADM23019', 'Pradeep R', 'Rajamani R', 'Sumathi R', '9876500019', '9876500069', 'M', '20000000036', '2005-07-11', 'SC', 'SC', 'Theni', 'Theni', 'pradeep@student.nscet.edu', '34, SC Colony, Theni', 'O+', 'Tamil', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government'),
('23CSE020', 'REG23020', 'ADM23020', 'Monica A', 'Antony A', 'Rosa A', '9876500020', '9876500070', 'F', '20000000037', '2005-11-08', 'OC', 'Christian', 'Theni', 'Theni', 'monica@student.nscet.edu', '89, Church Street, Theni', 'AB-', 'English', 'Theni Bus Stand', 'College Bus', 'Tamil Nadu', 'Government');

-- ============================
-- STUDENT DETAILS (semester enrollment)
-- ============================
INSERT INTO admin_student_details (student_id, semester, caste_category, department_id, quota_id, degree, section, academic_year, admission_year) VALUES
(1, 3, 'OC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(2, 3, 'BC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(3, 3, 'MBC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(4, 3, 'OC', 4, 2, 'B.E.', 'A', '2023-24', 2023),
(5, 3, 'SC', 4, 4, 'B.E.', 'A', '2023-24', 2023),
(6, 3, 'OC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(7, 3, 'BC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(8, 3, 'OC', 4, 2, 'B.E.', 'B', '2023-24', 2023),
(9, 3, 'MBC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(10, 3, 'BC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(11, 1, 'OC', 4, 1, 'B.E.', 'A', '2024-25', 2024),
(12, 1, 'BC', 4, 1, 'B.E.', 'A', '2024-25', 2024),
(13, 1, 'SC', 4, 4, 'B.E.', 'A', '2024-25', 2024),
(14, 1, 'OC', 4, 2, 'B.E.', 'A', '2024-25', 2024),
(15, 1, 'MBC', 4, 1, 'B.E.', 'A', '2024-25', 2024),
(16, 3, 'OC', 3, 1, 'B.E.', 'A', '2023-24', 2023),
(17, 3, 'BC', 3, 1, 'B.E.', 'A', '2023-24', 2023),
(18, 3, 'MBC', 3, 1, 'B.E.', 'A', '2023-24', 2023),
(19, 3, 'OC', 3, 1, 'B.E.', 'A', '2023-24', 2023),
(20, 3, 'OC', 1, 1, 'B.E.', 'A', '2023-24', 2023),
(21, 3, 'BC', 1, 1, 'B.E.', 'A', '2023-24', 2023),
(22, 3, 'OC', 18, 1, 'B.E.', 'A', '2023-24', 2023),
(23, 3, 'BC', 18, 1, 'B.E.', 'A', '2023-24', 2023),
(24, 3, 'OC', 20, 1, 'B.E.', 'A', '2023-24', 2023),
(25, 3, 'OC', 20, 2, 'B.E.', 'A', '2023-24', 2023),
(26, 3, 'OC', 21, 1, 'B.E.', 'A', '2023-24', 2023),
(27, 3, 'BC', 21, 1, 'B.E.', 'A', '2023-24', 2023),
(28, 3, 'MBC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(29, 3, 'OC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(30, 3, 'SC', 4, 4, 'B.E.', 'B', '2023-24', 2023),
(31, 3, 'OC', 4, 2, 'B.E.', 'B', '2023-24', 2023),
(32, 3, 'BC', 4, 1, 'B.E.', 'B', '2023-24', 2023),
(33, 3, 'OC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(34, 3, 'OC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(35, 3, 'BC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(36, 3, 'MBC', 4, 1, 'B.E.', 'A', '2023-24', 2023),
(37, 3, 'SC', 4, 4, 'B.E.', 'A', '2023-24', 2023);

-- ============================
-- FEES DETAILS (fee structure for CSE dept)
-- ============================
INSERT INTO admin_fees_details (from_date, to_date, degree, semester, quota_id, department_id, dept_type, admission_type, fees_name_id, amount) VALUES
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 4, 'Regular', 'Government', 1, 45000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 4, 'Regular', 'Government', 4, 8000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 4, 'Regular', 'Government', 3, 2000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 4, 'Regular', 'Government', 5, 3000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 4, 'Regular', 'Government', 2, 1500),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 4, 'Regular', 'Government', 6, 12000),
('2024-06-01', '2025-05-31', 'B.E.', 1, 1, 4, 'Regular', 'Government', 1, 45000),
('2024-06-01', '2025-05-31', 'B.E.', 1, 1, 4, 'Regular', 'Government', 4, 8000),
('2024-06-01', '2025-05-31', 'B.E.', 1, 1, 4, 'Regular', 'Government', 3, 2000),
('2024-06-01', '2025-05-31', 'B.E.', 1, 2, 4, 'Regular', 'Management', 1, 85000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 3, 'Regular', 'Government', 1, 45000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 3, 'Regular', 'Government', 4, 8000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 1, 'Regular', 'Government', 1, 45000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 18, 'Regular', 'Government', 1, 45000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 20, 'Regular', 'Government', 1, 45000),
('2024-06-01', '2025-05-31', 'B.E.', 3, 1, 21, 'Regular', 'Government', 1, 45000);

-- ============================
-- FEE RECEIPTS (recent transactions)
-- ============================
INSERT INTO admin_fee_receipts (receipt_number, student_id, student_type, receipt_date, academic_year, payment_mode, base_account, total_amount, status) VALUES
('REC20250801001', 1, 'Regular', '2025-08-01', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250801002', 2, 'Regular', '2025-08-01', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250801003', 3, 'Regular', '2025-08-01', '2025-26', 'ONLINE', 'Federal Online', 45000.00, 'ACTIVE'),
('REC20250802001', 5, 'Regular', '2025-08-02', '2025-26', 'CASH', 'TMB Main', 25000.00, 'ACTIVE'),
('REC20250802002', 6, 'Regular', '2025-08-02', '2025-26', 'DD', 'Federal Bank', 45000.00, 'ACTIVE'),
('REC20250803001', 7, 'Regular', '2025-08-03', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250803002', 16, 'Regular', '2025-08-03', '2025-26', 'ONLINE', 'Federal Online', 45000.00, 'ACTIVE'),
('REC20250804001', 4, 'Management', '2025-08-04', '2025-26', 'DD', 'Federal Bank', 85000.00, 'ACTIVE'),
('REC20250804002', 8, 'Management', '2025-08-04', '2025-26', 'ONLINE', 'Federal Online', 85000.00, 'ACTIVE'),
('REC20250805001', 9, 'Regular', '2025-08-05', '2025-26', 'CASH', 'TMB Main', 35000.00, 'ACTIVE'),
('REC20250805002', 10, 'Regular', '2025-08-05', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250805003', 20, 'Regular', '2025-08-05', '2025-26', 'ONLINE', 'Federal Online', 45000.00, 'ACTIVE'),
('REC20250806001', 11, 'Regular', '2025-08-06', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250806002', 12, 'Regular', '2025-08-06', '2025-26', 'DD', 'Canara Civil', 45000.00, 'ACTIVE'),
('REC20250806003', 17, 'Regular', '2025-08-06', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250807001', 22, 'Regular', '2025-08-07', '2025-26', 'ONLINE', 'Federal Online', 45000.00, 'ACTIVE'),
('REC20250807002', 24, 'Regular', '2025-08-07', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250807003', 26, 'Regular', '2025-08-07', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250808001', 14, 'Management', '2025-08-08', '2025-26', 'DD', 'Federal Bank', 85000.00, 'ACTIVE'),
('REC20250808002', 25, 'Management', '2025-08-08', '2025-26', 'ONLINE', 'Federal Online', 85000.00, 'ACTIVE'),
('REC20250808003', 28, 'Regular', '2025-08-08', '2025-26', 'CASH', 'TMB Main', 45000.00, 'ACTIVE'),
('REC20250808004', 31, 'Regular', '2025-08-08', '2025-26', 'CASH', 'TMB Main', 25000.00, 'ACTIVE'),
('REC20250808005', 13, 'Regular', '2025-08-08', '2025-26', 'CASH', 'TMB Main', 25000.00, 'ACTIVE');

-- ============================
-- FEE RECEIPT ITEMS
-- ============================
INSERT INTO admin_fee_receipt_items (receipt_id, fees_name_id, amount, allocated_to) VALUES
(1, 1, 45000.00, 'CASH'),
(2, 1, 45000.00, 'CASH'),
(3, 1, 45000.00, 'ONLINE'),
(4, 1, 25000.00, 'CASH'),
(5, 1, 45000.00, 'DD'),
(6, 1, 45000.00, 'CASH'),
(7, 1, 45000.00, 'ONLINE'),
(8, 1, 85000.00, 'DD'),
(9, 1, 85000.00, 'ONLINE'),
(10, 1, 35000.00, 'CASH'),
(11, 1, 45000.00, 'CASH'),
(12, 1, 45000.00, 'ONLINE'),
(13, 1, 45000.00, 'CASH'),
(14, 1, 45000.00, 'DD'),
(15, 1, 45000.00, 'CASH'),
(16, 1, 45000.00, 'ONLINE'),
(17, 1, 45000.00, 'CASH'),
(18, 1, 45000.00, 'CASH'),
(19, 1, 85000.00, 'DD'),
(20, 1, 85000.00, 'ONLINE'),
(21, 1, 45000.00, 'CASH'),
(22, 1, 25000.00, 'CASH'),
(23, 1, 25000.00, 'CASH');
