#include "InfoCheckResForm.h"
#include "ui_InfoCheckResForm.h"

InfoCheckResForm::InfoCheckResForm(QWidget *parent) : QWidget(parent), ui(new Ui::InfoCheckResWidget)
{
    ui->setupUi(this);
    this->move((parent->width() - this->width()) / 2, (parent->width() - this->width()) / 2);
}

InfoCheckResForm::~InfoCheckResForm()
{
    delete ui;
}

void InfoCheckResForm::on_pushButton_clicked()
{
    this->setVisible(false);
}

// widget拖动
void InfoCheckResForm::mousePressEvent(QMouseEvent *event)
{
    ismoveing = true;
    moveposition = event->globalPos() - pos();
    return QWidget::mousePressEvent(event);
}
void InfoCheckResForm::mouseMoveEvent(QMouseEvent *event)
{
    if(ismoveing && (event->buttons() && Qt::LeftButton) &&
      (event->globalPos()-moveposition).manhattanLength() > QApplication::startDragDistance())
    {
        move(event->globalPos()-moveposition);
        moveposition = event->globalPos() - pos();
    }
    return QWidget::mouseMoveEvent(event);
}
void InfoCheckResForm::mouseReleaseEvent(QMouseEvent *event)
{
    ismoveing = false;
}
