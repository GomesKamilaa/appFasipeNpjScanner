package com.example.fasipemobilej.database;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.database.sqlite.SQLiteDatabase;

import javax.xml.namespace.QName;


public class SQLDatabase {

    public String sqlCriarTabelaProcedimento(){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS procedimento ");
        sql.append(" ( ");
        sql.append(" cod_proced INTEGER, ");
        sql.append(" descr_proced TEXT NOT NULL, ");
        sql.append(" val_proced NUMERIC NOT NULL, ");
        sql.append(" PRIMARY KEY(cod_proced AUTOINCREMENT) ");
        sql.append(" ); ");

        return sql.toString();
    }

    public String sqlCriarTabelaProfissional(){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS professional ");
        sql.append(" ( ");
        sql.append(" cod_prof INTEGER NOT NULL, ");
        sql.append(" nome_prof TEXT, ");
        sql.append(" tipo_prof INTEGER, ");
        sql.append(" sup_prof INTEGER, ");
        sql.append(" status_prof INTEGER, ");
        sql.append(" cons_prof INTEGER, ");
        sql.append(" senha_prof INTEGER, ");
        sql.append(" PRIMARY KEY(cod_prof) ");
        sql.append(" ); ");

        return sql.toString();
    }

    public String sqlCriarTabelaProntuario(){
        StringBuilder sql = new StringBuilder();
        sql.append( " CREATE TABLE IF NOT EXISTS prontuario ");
        sql.append( " ( ");
        sql.append( " cpf_pac INTEGER NOT NULL, ");
        sql.append( " cod_espec INTEGER, ");
        sql.append( " cod_prof INTEGER, ");
        sql.append( " cod_proced INTEGER, ");
        sql.append( " dat_proced TEXT NOT NULL, ");
        sql.append( " descr_proced TEXT NOT NULL, ");
        sql.append( " link_proced TEXT NOT NULL, ");
        sql.append( " aut_vis_pac INTEGER NOT NULL, ");
        sql.append( " FOREIGN KEY(cod_proced) REFERENCES procedimento(cod_proced), ");
        sql.append( " FOREIGN KEY(cod_prof) REFERENCES professional(cod_prof), ");
        sql.append( " FOREIGN KEY(cpf_pac) REFERENCES paciente(cpf_pac), ");
        sql.append( " PRIMARY KEY(cpf_pac)");
        sql.append( " ); ");

        return sql.toString();
    }


    public String sqlCriarTabelaPaciente(){

        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS paciente ");
        sql.append( " ( ");
        sql.append(" nome_pac TEXT PRIMARY KEY NOT NULL, ");
        sql.append(" cod_pac INTEGER NOT NULL, ");
        sql.append(" tel_pac INTEGER NOT NULL, ");
        sql.append(" cep_pac INTEGER, ");
        sql.append(" logra_pac TEXT, ");
        sql.append(" num_logra_pac INTEGER, ");
        sql.append(" compl_pac TEXT, ");
        sql.append(" bairro_pac TEXT NOT NULL, ");
        sql.append(" cidade_pac TEXT NOT NULL, ");
        sql.append(" uf_pac TEXT NOT NULL, ");
        sql.append(" rg_pac INTEGER NOT NULL, ");
        sql.append(" est_rg_pac TEXT, ");
        sql.append(" nome_mae_pac TEXT, ");
        sql.append(" data_nasc_pac TEXT, ");
        sql.append(" PRIMARY KEY(cpf_pac) ");
        sql.append(" ); ");

        return sql.toString();
    }





}
