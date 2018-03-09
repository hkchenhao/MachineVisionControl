#ifndef FILEMANAGERFORM_H
#define FILEMANAGERFORM_H
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
    class FileManagerWidget;
}

class FileManagerForm : public QWidget
{
    Q_OBJECT
public:
    explicit FileManagerForm(QWidget* parent = nullptr);
    ~FileManagerForm();
signals:

private slots:
    void on_pushButton_Esc_clicked();

private:
    Ui::FileManagerWidget* ui;
};

#endif
