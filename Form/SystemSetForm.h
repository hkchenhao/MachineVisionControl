#ifndef SYSTEMSETFORM_H
#define SYSTEMSETFORM_H
#include <QWidget>
#include <QMessageBox>
#include <QDate>
#include <QDebug>
#include <QVector>
#include <QString>
#include <QList>
#include <QMap>

namespace Ui
{
    class SystemSetWidget;
}
class QJsonAnalysis;

class SystemSetForm : public QWidget
{
    Q_OBJECT
public:
    explicit SystemSetForm(QWidget* parent = nullptr);
    ~SystemSetForm();
signals:

private slots:
    void on_pushButton_Esc_clicked();
    void on_pushButton_Revise_23_clicked();
    void on_pushButton_Revise_24_clicked();
    void on_horizontalSlider_3_valueChanged(int value);
    void on_pushButton_Revise_25_clicked();
    void on_pushButton_Revise_26_clicked();
    void on_pushButton_Revise_18_clicked();
    void on_pushButton_Revise_19_clicked();
    void on_tabWidget_currentChanged(int index);

private:
    Ui::SystemSetWidget* ui;
    QJsonAnalysis* p_cameracalibrationJsonInfo;
    QJsonAnalysis* p_lightconfigJsonInfo;
};

#endif
