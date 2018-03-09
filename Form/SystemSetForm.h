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

class SystemSetForm : public QWidget
{
    Q_OBJECT
public:
    explicit SystemSetForm(QWidget* parent = nullptr);
    ~SystemSetForm();
signals:

private slots:

    void on_pushButton_Esc_clicked();

private:
    Ui::SystemSetWidget* ui;
};

#endif
